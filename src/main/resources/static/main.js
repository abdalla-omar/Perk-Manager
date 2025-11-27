$(function() {
    let currentUser = null;

    // --- Utility functions ---
    function getAuthFields() {
        return {
            email: $('#authEmail').val().trim(),
            password: $('#authPassword').val().trim()
        };
    }

    function clearAuthFields() {
        $('#authEmail').val('');
        $('#authPassword').val('');
    }

    function refreshUserData() {
        if (!currentUser) return;

        // Get user's matching perks
        api.getMatchingPerks(currentUser.id).then(ui.renderPerks);
        api.getAllPerks().then(perks => ui.renderAllPerks(perks, currentUser));
        // Get user profile
        api.getProfile(currentUser.id).then(profileData => {
            const memberships = profileData.memberships || [];
            ui.renderProfile(memberships);
            ui.updateMembershipOptions(memberships);
        });
    }

    function setCurrentUser(user) {
        currentUser = user;
        ui.setAuthUI(!!user);
        ui.setCurrentUserEmail(user ? user.email : null);

        if (user) {
            // Fetch user's perks and store them in currentUser.perks
            api.getMatchingPerks(user.id).then(perks => {
                currentUser.perks = perks.map(p => p.id); // save perk IDs
                ui.renderPerks(perks);

                // Also fetch all perks for rendering
                api.getAllPerks().then(allPerks => ui.renderAllPerks(allPerks, currentUser));
            });

            // Fetch user profile (memberships, etc.)
            api.getProfile(user.id).then(profileData => {
                const memberships = profileData.memberships || [];
                ui.renderProfile(memberships);
                ui.updateMembershipOptions(memberships);
            });

        } else {
            $('#userProfile').empty();
            $('#userPerks').empty();
        }
    }


    // Initial load
    api.getUsers().then(ui.renderUsers);
    api.getAllPerks().then(ui.renderAllPerks);
    ui.setAuthUI(false); // ensure appSection is hidden, login/signup visible

    // Login using shared fields
    $('#loginButton').click(function () {
        const creds = getAuthFields();

        if (!creds.email || !creds.password) {
            alert("Enter email and password to log in.");
            return;
        }

        api.login(creds)
            .then(user => {
                const normalizedUser = { id: user.id || user.userId, email: user.email };
                alert(`Logged in as ${normalizedUser.email}`);
                setCurrentUser(normalizedUser);
                clearAuthFields();
            })
            .catch(xhr => {
                alert('Login failed: ' + xhr.responseText);
            });

    });



    // --- Sign up ---
    $('#signupButton').click(function () {
        const data = getAuthFields();
        if (!data.email || !data.password) return alert("Enter email and password.");

        api.createUser(data)
            .then(userProfile => {
                const user = { id: userProfile.userId, email: userProfile.email };
                clearAuthFields();
                api.getUsers().then(ui.renderUsers);
                setCurrentUser(user);
            })
            .catch(xhr => alert('Sign up failed: ' + xhr.responseText));
    });

    // --- Logout ---
    $('#logoutButton').click(() => setCurrentUser(null));

    // --- Create Perk ---
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

        api.createPerk(currentUser.id, perk)
            .then(createdPerk => {
                alert(`Perk created! Net Score: ${createdPerk.netScore}, Active: ${createdPerk.active}`);
                $('#createPerkForm')[0].reset();

                //Add the newly created perk to currentUser.perks
                if (!currentUser.perks) currentUser.perks = [];
                currentUser.perks.push(createdPerk.id);
                //Refresh the perks display
                api.getAllPerks().then(perks => ui.renderAllPerks(perks, currentUser));
                api.getMatchingPerks(currentUser.id).then(ui.renderPerks);
            })
            .catch(err => {
                console.error(err);
                alert('Failed to create perk: ' + (err.responseJSON?.message || err.responseText || 'Unknown error'));
            });
    });

    // --- Add Membership ---
    $('#addMembershipForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) return alert('Please log in first.');

        const membership = $('#membershipSelect').val().trim();
        if (!membership) return;

        api.addMembership(currentUser.id, membership)
            .then(() => {
                alert(`Membership ${membership} added!`);
                $('#addMembershipForm')[0].reset();
                refreshUserData();
            })
            .catch(err => {
                console.error(err);
                alert('Failed to add membership: ' + (err.responseText || err.statusText || 'Unknown error'));
            });
    });

    // --- Show top perks ---
    $('#showTopPerks').click(function() {
        api.getPerksByVotes().then(perks => ui.renderAllPerks(perks, currentUser));
    });

    // --- Change password ---
    $('#changePasswordButton').click(() => $('#changePasswordModal').removeClass('hidden'));
    $('#cancelChangePassword').click(() => {
        $('#changePasswordModal').addClass('hidden');
        $('#changePasswordForm')[0].reset();
    });

    $('#changePasswordForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) return alert('Please log in first.');

        const currentPassword = $('#currentPassword').val().trim();
        const newPassword = $('#newPassword').val().trim();
        const confirmPassword = $('#confirmPassword').val().trim();

        if (!currentPassword || !newPassword || !confirmPassword) return alert('All fields are required.');
        if (newPassword !== confirmPassword) return alert('New password and confirmation do not match.');
        if (currentPassword === newPassword) return alert('New password must be different.');

        api.changePassword(currentUser.id, currentPassword, newPassword)
            .then(() => {
                alert('Password changed successfully! Redirecting...');
                $('#changePasswordModal').addClass('hidden');
                $('#changePasswordForm')[0].reset();
                setTimeout(() => setCurrentUser(null), 2000);
            })
            .catch(xhr => alert('Error: ' + (xhr.responseText || 'Failed to change password')));
    });
});
