// Renders data and updates the page elements dynamically
// Updated to work with CQRS Read Models + per-user voting
const ui = {
    renderUsers: (users) => {
        const $list = $('#userList').empty();
        if (!users || !users.length) {
            return $list.append('<li>No users found</li>');
        }
        users.forEach(u => $list.append(`<li>ID: ${u.id} - ${u.email}</li>`));
    },

    /**
     * Your perks (for current user - shows matching perks from CQRS)
     * perks: array of PerkReadModel
     * currentUser: { id, email, perks? } or undefined
     */
    renderPerks: (perks, currentUser) => {
        const $list = $('#userPerks').empty();
        if (!perks || !perks.length) {
            return $list.append('<li>No perks available.</li>');
        }

        const hasValidUser = currentUser && typeof currentUser === 'object' && currentUser.id;

        perks.forEach(p => {
            const $li = $('<li>');

            // CQRS Read Model includes netScore and active status
            const activeStatus = p.active ? '✓' : '✗';
            $li.append(`${p.description} - ${p.product} - ${p.membership} `);
            $li.append(`<em>(Net: ${p.netScore}, Active: ${activeStatus})</em> `);

            const $count = $(`<span>(↑${p.upvotes} ↓${p.downvotes})</span>`);
            $li.append($count);

            // Upvote button
            const $upBtn = $('<button type="button">👍 Upvote</button>');
            $upBtn.click(() => {
                if (!hasValidUser) {
                    alert('Please log in first.');
                    return;
                }
                api.upvotePerk(p.id, currentUser.id)
                    .then(updated => {
                        $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`);
                        console.log('Upvote event published to Kafka (toggle logic applied).');
                    })
                    .catch(err => {
                        console.error('Error upvoting perk:', err);
                        alert('Failed to upvote perk.');
                    });
            });

            // Downvote button
            const $downBtn = $('<button type="button">👎 Downvote</button>');
            $downBtn.click(() => {
                if (!hasValidUser) {
                    alert('Please log in first.');
                    return;
                }
                api.downvotePerk(p.id, currentUser.id)
                    .then(updated => {
                        $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`);
                        console.log('Downvote event published to Kafka (toggle logic applied).');
                    })
                    .catch(err => {
                        console.error('Error downvoting perk:', err);
                        alert('Failed to downvote perk.');
                    });
            });

            $li.append(' ');
            $li.append($upBtn);
            $li.append(' ');
            $li.append($downBtn);

            $list.append($li);
        });
    },

    /**
     * All perks (from everyone) - using CQRS Read Model
     * perks: array of PerkReadModel
     * currentUser: { id, email, perks? } or undefined
     */
    renderAllPerks: (perks, currentUser) => {
        const $list = $('#allPerks').empty();
        if (!perks || !perks.length) {
            return $list.append('<li>No perks available.</li>');
        }

        const hasValidUser = currentUser && typeof currentUser === 'object' && currentUser.id;

        perks.forEach(p => {
            const $li = $('<li>');

            const postedBy = p.postedByEmail || 'Unknown user';
            const activeStatus = p.active ? '✓ Active' : '✗ Inactive';

            $li.append(`${p.description} - ${p.product} - ${p.membership} `);
            $li.append(`<small>(by ${postedBy})</small> `);
            $li.append(`<em>[Net: ${p.netScore}, ${activeStatus}]</em> `);

            const $count = $(`<span>(↑${p.upvotes} ↓${p.downvotes})</span>`);
            $li.append($count);

            // Upvote button
            const $upBtn = $('<button type="button">👍</button>');
            $upBtn.click(() => {
                if (!hasValidUser) {
                    alert('Please log in first.');
                    return;
                }
                api.upvotePerk(p.id, currentUser.id)
                    .then(updated => {
                        $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`);
                        console.log('✓ PerkUpvotedEvent published to Kafka (toggle logic applied).');
                    })
                    .catch(err => {
                        console.error('Error upvoting perk:', err);
                        alert('Failed to upvote perk.');
                    });
            });
            $li.append($upBtn);

            // Downvote button
            const $downBtn = $('<button type="button">👎</button>');
            $downBtn.click(() => {
                if (!hasValidUser) {
                    alert('Please log in first.');
                    return;
                }
                api.downvotePerk(p.id, currentUser.id)
                    .then(updated => {
                        $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`);
                        console.log('✓ PerkDownvotedEvent published to Kafka (toggle logic applied).');
                    })
                    .catch(err => {
                        console.error('Error downvoting perk:', err);
                        alert('Failed to downvote perk.');
                    });
            });
            $li.append($downBtn);

            // Only show the "Add Perk" button if:
            // - we have a currentUser
            // - and the user does NOT already own this perk
            const userPerks = (currentUser && Array.isArray(currentUser.perks))
                ? currentUser.perks
                : [];

            if (hasValidUser && !userPerks.includes(p.id)) {
                const $addBtn = $('<button type="button">Add Perk</button>');
                $addBtn.click(() => {
                    api.addPerkToUser(currentUser.id, p.id)
                        .then(() => {
                            alert('Perk added to your profile!');
                            // Refresh the current user's perks and re-render
                            api.getMatchingPerks(currentUser.id).then(perksForUser => {
                                ui.renderPerks(perksForUser, currentUser);
                                currentUser.perks = perksForUser.map(perk => perk.id);
                                api.getAllPerks().then(allPerks => ui.renderAllPerks(allPerks, currentUser));
                            });
                        })
                        .catch(err => {
                            console.error('Error adding perk:', err);
                            alert('Failed to add perk: ' + (err.responseText || err));
                        });
                });
                $li.append($addBtn);
            }

            $list.append($li);
        });
    },

    renderProfile: (memberships) => {
        const $list = $('#userProfile').empty();
        if (!memberships || !memberships.length) {
            return $list.append('<li>No memberships yet.</li>');
        }
        memberships.forEach(m => $list.append(`<li>${m}</li>`));
    },

    updateMembershipOptions: (memberships) => {
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
