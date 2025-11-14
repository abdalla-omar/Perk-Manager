// Handles user interactions, form submissions, and updates the UI dynamically
$(function() {
    let currentUser = null;

    // Initial load
    api.getUsers().then(ui.renderUsers);

    // Create user
    $('#createUserForm').submit(function(e) {
        e.preventDefault();
        const data = {
            email: $('#userEmail').val().trim(),
            password: $('#userPassword').val().trim()
        };
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
        const creds = {
            email: $('#loginEmail').val().trim(),
            password: $('#loginPassword').val().trim()
        };

        api.login(creds)
            .then(user => {
                currentUser = user;
                alert(`Logged in as ${user.email}`);
                api.getUserPerks(user.id).then(ui.renderPerks);
                api.getProfile(user.id).then(m => {
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
