// Handles user interactions, form submissions, and updates the UI dynamically
// Updated to work with CQRS API response formats
$(function() {
    let currentUser = null;

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

        // CQRS: Get matching perks for user's profile
        api.getMatchingPerks(currentUser.id).then(ui.renderPerks);

        // CQRS: Get user profile with memberships
        api.getProfile(currentUser.id).then(profileData => {
            ui.renderProfile(profileData.memberships || []);
            ui.updateMembershipOptions(profileData.memberships || []);
        });
    }

    function setCurrentUser(user) {
        currentUser = user;

        // update UI visibility + email
        ui.setAuthUI(!!user);
        ui.setCurrentUserEmail(user ? user.email : null);

        if (user) {
            refreshUserData();
        } else {
            // clear user-specific sections
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
                alert(`Logged in as ${user.email}`);
                setCurrentUser(user);
                clearAuthFields();
            })
            .catch(xhr => {
                alert('Login failed: ' + xhr.responseText);
            });
    });

    // Sign up using shared fields
    $('#signupButton').click(function () {
        const data = getAuthFields();

        if (!data.email || !data.password) {
            alert("Enter email and password to sign up.");
            return;
        }

        api.createUser(data)
            .then(userProfile => {
                // CQRS returns UserProfileReadModel: { userId, email, profileId, memberships }
                const user = {
                    id: userProfile.userId,
                    email: userProfile.email
                };
                clearAuthFields();
                api.getUsers().then(ui.renderUsers);
                setCurrentUser(user);
            })
            .catch(xhr => {
                alert('Sign up failed: ' + xhr.responseText);
            });
    });

    // Logout
    $('#logoutButton').click(function() {
        setCurrentUser(null);
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

        api.createPerk(currentUser.id, perk).then((createdPerk) => {
            // CQRS returns PerkReadModel with additional computed fields
            console.log('Perk created:', createdPerk);
            alert(`Perk created! Net Score: ${createdPerk.netScore}, Active: ${createdPerk.active}`);

            this.reset();

            // Refresh both matching perks and the global list
            api.getMatchingPerks(currentUser.id).then(ui.renderPerks);
            api.getAllPerks().then(ui.renderAllPerks);
        }).catch(err => {
            console.error('Error creating perk:', err);
            alert('Failed to create perk: ' + (err.responseJSON?.message || err.responseText || 'Unknown error'));
        });
    });

    // Add membership to profile
    $('#addMembershipForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) return alert('Please log in first.');

        const membership = $('#membershipSelect').val().trim();
        if (!membership) return;

        api.addMembership(currentUser.id, membership).then(() => {
            alert(`Membership ${membership} added! (Event published to Kafka)`);
            this.reset();

            // Refresh profile
            api.getProfile(currentUser.id).then(profileData => {
                ui.renderProfile(profileData.memberships || []);
                ui.updateMembershipOptions(profileData.memberships || []);
            });

            // Refresh matching perks (now that profile changed)
            api.getMatchingPerks(currentUser.id).then(ui.renderPerks);
        }).catch(err => {
            console.error('Error adding membership:', err);
            const errorMsg = err.responseText || err.statusText || 'Unknown error';
            alert('Failed to add membership: ' + errorMsg);
        });
    });

    // Add button to show perks sorted by votes (CQRS feature!)
    $('#showTopPerks').click(function() {
        api.getPerksByVotes().then(perks => {
            console.log('Top perks by votes:', perks);
            ui.renderAllPerks(perks);
        });
    });
});
