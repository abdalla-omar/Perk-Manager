// Renders data and updates the page elements dynamically
// Updated to work with CQRS Read Models + per-user voting
const TOAST_TIMEOUT = 3600; // toast display duration in ms
let toastTimer; // timer for hiding toast

const ui = {
    // Show a temporary toast message for user feedback
    showToast(message, type = 'info') {
        const $box = $('#messageBox');
        $box.text(message).attr('data-type', type).removeClass('hidden');
        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => $box.addClass('hidden'), TOAST_TIMEOUT);
    },

    renderUsers(users) {
        const $list = $('#userList').empty();
        if (!users || !users.length) {
            return $list.append('<li>No community members yet.</li>');
        }
        users.forEach(user => {
            const $item = $('<li>').text(user.email || 'Unknown');
            $list.append($item);
        });
    },

    /**
     * Your perks (perks you own/created)
     * perkBuckets: object with 'Your Perks' key containing your owned perks
     * currentUser: { id, email, perks? } or undefined
     */
    renderPerks(perkBuckets, currentUser) {
        const $grid = $('#userPerks').empty();
        if (!perkBuckets || typeof perkBuckets !== 'object') {
            return $grid.append('<p class="empty-state">No perks in your profile yet.</p>');
        }

        const yourPerks = perkBuckets['Your Perks'] || [];
        const perksArray = Array.isArray(yourPerks) ? yourPerks : [];

        if (perksArray.length === 0) {
            return $grid.append('<p class="empty-state">No perks in your profile yet. Create one or add from All Perks!</p>');
        }

        perksArray.forEach(perk => $grid.append(createPerkCard(perk, currentUser, { context: 'owned' })));
    },

    /**
     * All perks (from everyone) - using CQRS Read Model
     * perks: array of PerkReadModel
     * currentUser: { id, email, perks? } or undefined
     */
    renderAllPerks: (perks, currentUser) => {
        const $grid = $('#allPerks').empty();
        if (!perks || !perks.length) {
            return $grid.append('<p class="empty-state">No perks have been published yet.</p>');
        }
        perks.forEach(perk => $grid.append(createPerkCard(perk, currentUser, { context: 'all' })));
    },

    renderProfile(memberships) {
        const $list = $('#userProfile').empty();
        if (!memberships || !memberships.length) {
            return $list.append('<li>No memberships yet.</li>');
        }
        memberships.forEach(m => $list.append(`<li>${m}</li>`));
    },

    updateMembershipOptions(memberships) {
        const $select = $('#perkMembership')
            .empty()
            .append('<option value="">-- Select Membership --</option>');
        if (memberships && memberships.length) {
            memberships.forEach(m => $select.append(`<option value="${m}">${m}</option>`));
        }
    },

    setAuthUI(isLoggedIn) {
        // Public area (login + signup + existing users)
        $('#publicSection').toggle(!isLoggedIn);

        // App area + logout bar
        $('#appSection').toggleClass('hidden', !isLoggedIn);
        $('#logoutBar').toggleClass('hidden', !isLoggedIn);

        if (!isLoggedIn) {
            $('#currentUserEmail').text('');
        }
    },

    setCurrentUserEmail(email) {
        $('#currentUserEmail').text(email || '');
    }
};

// Create a perk card element
function createPerkCard(perk, currentUser, { context } = {}) {
    const $card = $('<article class="perk-card">');
    const hasValidUser = currentUser && currentUser.id;
    const userPerks = hasValidUser && Array.isArray(currentUser.perks) ? currentUser.perks : [];

    $card.append(`<span class="badge">${perk.membership}</span>`);
    $card.append(`<h3>${perk.description}</h3>`);

    const $meta = $('<div class="perk-meta">');
    $meta.append(`<span>${perk.product}</span>`);
    $meta.append(`<span class="${perk.active ? 'positive' : 'negative'}">${perk.active ? 'Active' : 'Inactive'}</span>`);
    $card.append($meta);

    const postedBy = perk.postedByEmail || 'Unknown member';
    $card.append(`<p class="muted small">by ${postedBy}</p>`);

    if (perk.startDate || perk.endDate) {
        const windowText = formatDateWindow(perk.startDate, perk.endDate);
        if (windowText) {
            $card.append(`<p class="muted small">${windowText}</p>`);
        }
    }

    const $voteRow = $('<div class="vote-row">');
    const $score = $(`<strong>Net ${perk.netScore ?? 0}</strong>`);
    const $counts = $(`<span class="muted">↑${perk.upvotes} ↓${perk.downvotes}</span>`);
    $voteRow.append($score);

    const $controls = $('<div class="vote-controls">');
    const $upBtn = $('<button type="button" class="vote-btn positive" aria-label="Upvote perk">👍</button>');
    const $downBtn = $('<button type="button" class="vote-btn negative" aria-label="Downvote perk">👎</button>');

    $upBtn.click(() => {
        if (!hasValidUser) {
            ui.showToast('Please log in first.', 'error');
            return;
        }
        api.upvotePerk(perk.id, currentUser.id)
            .then(updated => updateVoteDisplay(updated, $counts, $score))
            .catch(() => ui.showToast('Failed to upvote perk.', 'error'));
    });

    $downBtn.click(() => {
        if (!hasValidUser) {
            ui.showToast('Please log in first.', 'error');
            return;
        }
        api.downvotePerk(perk.id, currentUser.id)
            .then(updated => updateVoteDisplay(updated, $counts, $score))
            .catch(() => ui.showToast('Failed to downvote perk.', 'error'));
    });

    $controls.append($upBtn, $downBtn);
    $voteRow.append($counts, $controls);
    $card.append($voteRow);

    const $shareRow = $('<div class="perk-share">');
    const $shareBtn = $('<button type="button" class="share-btn">Share Perk</button>');
    $shareBtn.click(() => sharePerk(perk));
    $shareRow.append($shareBtn);

    const shouldAllowAdd = hasValidUser && context === 'all' && !userPerks.includes(perk.id);
    if (shouldAllowAdd) {
        const $addBtn = $('<button type="button" class="primary share-btn">Add to My Perks</button>');
        $addBtn.click(() => {
            api.addPerkToUser(currentUser.id, perk.id)
                .then(() => {
                    ui.showToast('Perk added to your profile!', 'success');
                    api.getMatchingPerks(currentUser.id).then(perkBuckets => {
                        currentUser.perkBuckets = perkBuckets;
                        currentUser.perks = (perkBuckets['Your Perks'] || []).map(p => p.id);
                        ui.renderPerks(perkBuckets, currentUser);
                        api.getAllPerks().then(allPerks => ui.renderAllPerks(allPerks, currentUser));
                    });
                })
                .catch(err => ui.showToast('Failed to add perk: ' + (err.responseText || 'Unknown error'), 'error'));
        });
        $shareRow.append($addBtn);
    }

    $card.append($shareRow);
    return $card;
}

// Share perk via Web Share API or copy to clipboard
function sharePerk(perk) {
    const shareText = `Check out this perk you can get with ${perk.membership}: ${perk.description} (${perk.product}). Found via PerkCollective!`;
    if (navigator.share) {
        navigator.share({ title: 'PerkCollective Perk', text: shareText })
            .then(() => ui.showToast('Shared via native share sheet!', 'success'))
            .catch(err => {
                if (err && err.name === 'AbortError') {
                    return;
                }
                copyToClipboard(shareText);
            });
        return;
    }
    copyToClipboard(shareText);
}

// Copy text to clipboard with fallback
function copyToClipboard(text) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text)
            .then(() => ui.showToast('Perk copied to clipboard!', 'success'))
            .catch(() => fallbackCopy(text));
        return;
    }
    fallbackCopy(text);
}

// Fallback method to copy text using a temporary textarea
function fallbackCopy(text) {
    const $temp = $('<textarea readonly class="visually-hidden">').val(text).appendTo('body');
    $temp[0].select();
    document.execCommand('copy');
    $temp.remove();
    ui.showToast('Perk copied to clipboard!', 'success');
}

// Update vote counts and score display
function updateVoteDisplay(updated, $counts, $score) {
    if (!updated) return;
    const upvotes = updated.upvotes ?? 0;
    const downvotes = updated.downvotes ?? 0;
    const net = updated.netScore ?? (upvotes - downvotes);
    $counts.text(`↑${upvotes} ↓${downvotes}`);
    $score.text(`Net ${net}`);
}

// Format start and end dates into a readable string
function formatDateWindow(start, end) {
    if (!start && !end) return '';
    if (start && end) {
        return `Valid ${start} – ${end}`;
    }
    if (start) {
        return `Starts ${start}`;
    }
    return `Ends ${end}`;
}
