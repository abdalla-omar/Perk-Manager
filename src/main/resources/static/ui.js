// Renders data and updates the page elements dynamically
// Updated to work with CQRS Read Models
const ui = {
    renderUsers: (users) => {
        const $list = $('#userList').empty();
        if (!users.length) return $list.append('<li>No users found</li>');
        users.forEach(u => $list.append(`<li>ID: ${u.id} - ${u.email}</li>`));
    },

    // Your perks (for current user - shows matching perks from CQRS)
    renderPerks: (perks) => {
        const $list = $('#userPerks').empty();
        if (!perks.length) return $list.append('<li>No perks available.</li>');

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
                api.upvotePerk(p.id)
                    .then(updated => {
                        $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`);
                        console.log('Upvote event published to Kafka!');
                    })
                    .catch(err => {
                        console.error('Error upvoting perk:', err);
                        alert('Failed to upvote perk.');
                    });
            });

            // Downvote button (new CQRS feature!)
            const $downBtn = $('<button type="button">👎 Downvote</button>');
            $downBtn.click(() => {
                api.downvotePerk(p.id)
                    .then(updated => {
                        $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`);
                        console.log('Downvote event published to Kafka!');
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

    // All perks (from everyone) - using CQRS Read Model
    renderAllPerks: (perks, currentUser) => {
        const $list = $('#allPerks').empty();
        if (!perks.length) return $list.append('<li>No perks available.</li>');

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
            $('<button type="button">👍</button>').click(() => {
                api.upvotePerk(p.id)
                    .then(updated => $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`))
                    .catch(err => alert('Failed to upvote perk.'));
            }).appendTo($li);

            // Downvote button
            $('<button type="button">👎</button>').click(() => {
                api.downvotePerk(p.id)
                    .then(updated => $count.text(`(↑${updated.upvotes} ↓${updated.downvotes})`))
                    .catch(err => alert('Failed to downvote perk.'));
            }).appendTo($li);

            //Only show the add perk button if the user doesn't own the perk already
            if (currentUser && !(currentUser.perks || []).includes(p.id)) {
                $('<button type="button">Add Perk</button>').click(() => {
                    api.addPerkToUser(currentUser.id, p.id)
                        .then(() => {
                            alert('Perk added to your profile!');
                            //Refresh the current user's perks
                            api.getMatchingPerks(currentUser.id).then(perks => {
                                ui.renderPerks(perks);
                                currentUser.perks = perks.map(perk => perk.id);
                                //Re-render all perks to remove Add button for this perk
                                api.getAllPerks().then(perks => ui.renderAllPerks(perks, currentUser));
                            });
                        })
                        .catch(err => alert('Failed to add perk: ' + (err.responseText || err)));
                }).appendTo($li);
            }

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
        // show/hide sections based on auth (loggedIn or not)
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
