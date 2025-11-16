// Renders data and updates the page elements dynamically
const ui = {
    renderUsers: (users) => {
        const $list = $('#userList').empty();
        if (!users.length) return $list.append('<li>No users found</li>');
        users.forEach(u => $list.append(`<li>ID: ${u.id} - ${u.email}</li>`));
    },

    showMessage : (msg, type) => {
        const $box = $('#messageBox');
        $box.text(msg);
        $box.removeClass().addClass(type).fadeIn();

        setTimeout(() => $box.fadeOut(), 1500);
    },

    // Your perks (for current user)
    renderPerks: (perks) => {
        const $list = $('#userPerks').empty();
        if (!perks.length) return $list.append('<li>No perks available.</li>');

        perks.forEach(p => {
            const $li = $('<li>');

            $li.append(`${p.description} - ${p.product} - ${p.membership} `);

            const $count = $(
                `<span class="perk-upvotes" data-perk-id="${p.id}">(Upvotes: ${p.upvotes})</span>`
            );
            $li.append($count);

            const $btn = $('<button type="button">Upvote</button>');
            $btn.click(() => {
                api.upvotePerk(p.id)
                    .then(updated => {
                        const id = updated.id ?? p.id; // fallback if backend doesn't send id
                        $(`.perk-upvotes[data-perk-id="${id}"]`)
                            .text(`(Upvotes: ${updated.upvotes})`);
                    })
                    .catch(err => {
                        console.error('Error upvoting perk:', err);
                        alert('Failed to upvote perk.');
                    });
            });

            $li.append(' ');
            $li.append($btn);

            $list.append($li);
        });
    },

    // 🔹 NEW: All perks (from everyone)
    renderAllPerks: (perks) => {
        const $list = $('#allPerks').empty();
        if (!perks.length) return $list.append('<li>No perks available.</li>');

        perks.forEach(p => {
            const $li = $('<li>');

            // Show who posted it as well
            const postedBy = p.postedBy?.email || 'Unknown user';
            $li.append(`${p.description} - ${p.product} - ${p.membership} (by ${postedBy}) `);

            const $count = $(
                `<span class="perk-upvotes" data-perk-id="${p.id}">(Upvotes: ${p.upvotes})</span>`
            );
            $li.append($count);

            const $btn = $('<button type="button">Upvote</button>');
            $btn.click(() => {
                api.upvotePerk(p.id)
                    .then(updated => {
                        const id = updated.id ?? p.id; // fallback if backend doesn't send id
                        $(`.perk-upvotes[data-perk-id="${id}"]`)
                            .text(`(Upvotes: ${updated.upvotes})`);
                    })
                    .catch(err => {
                        console.error('Error upvoting perk:', err);
                        alert('Failed to upvote perk.');
                    });
            });

            $li.append(' ');
            $li.append($btn);

            $list.append($li);
        });
    },

    renderProfile: (memberships) => {
        const $list = $('#userProfile').empty();
        if (!memberships?.length) return $list.append('<li>No memberships yet.</li>');
        memberships.forEach(m => $list.append(`<li>${m}</li>`));
    },

    updateMembershipOptions: (memberships) => {
        const $select = $('#perkMembership')
            .empty()
            .append(`<option value="">-- Select Membership --</option>`);
        memberships?.forEach(m => $select.append(`<option value="${m}">${m}</option>`));
    },

    setAuthUI(isLoggedIn) {
        // show/hide sections based on auth  (loggedIn or not)
        // Public area (login + signup + existing users)
        $('#publicSection').toggle(!isLoggedIn);

        // App area + logout bar
        $('#appSection').toggleClass('hidden', !isLoggedIn);
        $('#logoutBar').toggleClass('hidden', !isLoggedIn);

        // clear current user email when logged out
        if (!isLoggedIn) {
            $('#currentUserEmail').text('');
        }
    },

    setCurrentUserEmail(email) {
        $('#currentUserEmail').text(email || '');
    }
};
