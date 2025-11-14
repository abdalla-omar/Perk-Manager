// Handles user interactions, form submissions, and updates the UI dynamically
$(function() {
    let currentUser = null;

    // Initial load
    api.getUsers().then(ui.renderUsers);

    // Create user
    $('#createUserForm').submit(function(e) {
        e.preventDefault();
        const data = { email: $('#userEmail').val().trim(), password: $('#userPassword').val().trim() };
        api.createUser(data).then(user => {
            currentUser = user;
            this.reset();
            api.getUsers().then(ui.renderUsers);
            api.getUserPerks(currentUser.id).then(ui.renderPerks);
            api.getProfile(currentUser.id).then(m => {
                ui.renderProfile(m);
                ui.updateMembershipOptions(m);
            });
        });
    });

    // Login
    $('#loginForm').submit(function(e) {
        e.preventDefault();
        const creds = { email: $('#loginEmail').val().trim(), password: $('#loginPassword').val().trim() };

        api.login(creds).then(user => {
            currentUser = user;
            alert(`Logged in as ${user.email}`);
            api.getUserPerks(currentUser.id).then(ui.renderPerks);
            api.getProfile(currentUser.id).then(m => {
                ui.renderProfile(m);
                ui.updateMembershipOptions(m);
            });
        })
        .catch(xhr => alert('Login failed: ' + xhr.responseText))
        .always(() => this.reset());
    });

    // Create perk
    $('#createPerkForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) return alert('Please log in first.');

        const perk = {
            description: $('#perkDescription').val().trim(),
            membership: $('#perkMembership').val().trim(),
            product: $('#perkProduct').val().trim(),
            startDate: $('#perkStartDate').val(),
            endDate: $('#perkEndDate').val()
        };

        api.createPerk(currentUser.id, perk).then(() => {
            this.reset();
            api.getUserPerks(currentUser.id).then(ui.renderPerks);
        });
    });

    // Add membership to profile
    $('#addMembershipForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) return alert('Please log in first.');

        const membership = $('#membershipSelect').val().trim();
        if (!membership) return;

        api.addMembership(currentUser.id, membership).then(() => {
            alert(`Membership ${membership} added!`);
            this.reset();
            api.getProfile(currentUser.id).then(m => {
                ui.renderProfile(m);
                ui.updateMembershipOptions(m);
            });
        });
    });
});


// Renders data and updates the page elements dynamically
const ui = {
    renderUsers: (users) => {
        const $list = $('#userList').empty();
        if (!users.length) return $list.append('<li>No users found</li>');
        users.forEach(u => $list.append(`<li>ID: ${u.id} - ${u.email}</li>`));
    },

    // 🔹 UPDATED: show upvote count + button
    renderPerks: (perks) => {
        const $list = $('#userPerks').empty();
        if (!perks.length) return $list.append('<li>No perks available.</li>');

        perks.forEach(p => {
            const $li = $('<li>');

            // Description + product + membership
            $li.append(`${p.description} - ${p.product} - ${p.membership} `);

            // Upvote count
            const $count = $(`<span>(Upvotes: ${p.upvotes})</span>`);
            $li.append($count);

            // Upvote button
            const $btn = $('<button type="button">').text('Upvote');
            $btn.click(() => {
                api.upvotePerk(p.id)
                    .then(updated => {
                        $count.text(`(Upvotes: ${updated.upvotes})`);
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
        const $select = $('#perkMembership').empty().append(`<option value="">-- Select Membership --</option>`);
        memberships?.forEach(m => $select.append(`<option value="${m}">${m}</option>`));
    }
};
