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

        api.getMatchingPerks(currentUser.id)
            .then(perkBuckets => {
                console.log('Received perkBuckets:', perkBuckets);
                currentUser.perkBuckets = perkBuckets;
                currentUser.perks = extractOwnedPerkIds(perkBuckets);
                ui.renderPerks(perkBuckets, currentUser);
            })
            .catch(err => {
                console.error('Failed to load matching perks:', err);
                ui.showToast('Could not load your perks.', 'error');
            });
        api.getAllPerks()
            .then(perks => ui.renderAllPerks(perks, currentUser))
            .catch(err => console.error('Failed to load all perks:', err));
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
            api.getMatchingPerks(user.id)
                .then(perkBuckets => {
                    console.log('Login - Received perkBuckets:', perkBuckets);
                    currentUser.perkBuckets = perkBuckets;
                    currentUser.perks = extractOwnedPerkIds(perkBuckets);
                    ui.renderPerks(perkBuckets, currentUser);
                    api.getAllPerks()
                        .then(allPerks => ui.renderAllPerks(allPerks, currentUser))
                        .catch(err => console.error('Failed to load all perks after login:', err));
                })
                .catch(err => {
                    console.error('Failed to load matching perks after login:', err);
                    ui.showToast('Could not load your perks.', 'error');
                });

            // Fetch user profile (memberships, etc.)
            api.getProfile(user.id)
                .then(profileData => {
                    const memberships = profileData.memberships || [];
                    ui.renderProfile(memberships);
                    ui.updateMembershipOptions(memberships);
                })
                .catch(err => {
                    console.error('Failed to load user profile:', err);
                });

        } else {
            $('#userProfile').empty();
            $('#userPerks').empty();
        }
    }

    function extractOwnedPerkIds(perkBuckets) {
        if (!perkBuckets || typeof perkBuckets !== 'object') return [];
        const owned = perkBuckets['Your Perks'] || [];
        const ownedArray = Array.isArray(owned) ? owned : [];
        return ownedArray.map(p => p.id);
    }


    // Initial load
    api.getUsers()
        .then(ui.renderUsers)
        .catch(err => {
            console.error('Failed to load users:', err);
            $('#userList').empty().append('<li>Could not load community members.</li>');
        });
    api.getAllPerks()
        .then(perks => ui.renderAllPerks(perks, null))
        .catch(err => console.error('Failed to load all perks:', err));
    ui.setAuthUI(false); // ensure appSection is hidden, login/signup visible

    // Login using shared fields
    $('#loginButton').click(function () {
        const creds = getAuthFields();

        if (!creds.email || !creds.password) {
            ui.showToast('Enter email and password to log in.', 'error');
            return;
        }

        api.login(creds)
            .then(user => {
                const normalizedUser = { id: user.id || user.userId, email: user.email };
                ui.showToast(`Logged in as ${normalizedUser.email}`, 'success');
                setCurrentUser(normalizedUser);
                clearAuthFields();
            })
            .catch(xhr => ui.showToast('Login failed: ' + (xhr.responseText || 'Unknown error'), 'error'));

    });



    // --- Sign up ---
    $('#signupButton').click(function () {
        const data = getAuthFields();
        if (!data.email || !data.password) {
            ui.showToast('Enter email and password.', 'error');
            return;
        }

        api.createUser(data)
            .then(userProfile => {
                const user = { id: userProfile.userId, email: userProfile.email };
                ui.showToast('Account created! You are now signed in.', 'success');
                clearAuthFields();
                api.getUsers().then(ui.renderUsers);
                setCurrentUser(user);
            })
            .catch(xhr => ui.showToast('Sign up failed: ' + (xhr.responseText || 'Unknown error'), 'error'));
    });

    // --- Logout ---
    $('#logoutButton').click(() => {
        setCurrentUser(null);
        ui.showToast('You have been logged out.', 'info');
    });

    // --- Create Perk ---
    $('#createPerkForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) {
            ui.showToast('Please log in first.', 'error');
            return;
        }

        const perk = {
            description: $('#perkDescription').val().trim(),
            membership: $('#perkMembership').val().trim(),
            product: $('#perkProduct').val().trim(),
            startDate: $('#perkStartDate').val(),
            endDate: $('#perkEndDate').val()
        };

        api.createPerk(currentUser.id, perk)
            .then(createdPerk => {
                ui.showToast('Perk created!', 'success');
                $('#createPerkForm')[0].reset();

                //Add the newly created perk to currentUser.perks
                if (!currentUser.perks) currentUser.perks = [];
                currentUser.perks.push(createdPerk.id);
                refreshUserData();
            })
            .catch(err => ui.showToast('Failed to create perk: ' + (err.responseJSON?.message || err.responseText || 'Unknown error'), 'error'));
    });

    // --- Add Membership ---
    $('#addMembershipForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) {
            ui.showToast('Please log in first.', 'error');
            return;
        }

        const membership = $('#membershipSelect').val().trim();
        if (!membership) return;

        api.addMembership(currentUser.id, membership)
            .then(() => {
                ui.showToast(`Membership ${membership} added!`, 'success');
                $('#addMembershipForm')[0].reset();
                refreshUserData();
            })
            .catch(err => ui.showToast('Failed to add membership: ' + (err.responseText || err.statusText || 'Unknown error'), 'error'));
    });

    // --- Show top perks ---
    $('#showTopPerks').click(function() {
        api.getPerksByVotes().then(perks => ui.renderAllPerks(perks, currentUser));
    });

    $('#filterMembership').change(function() {
        const membership = $(this).val();
        if (!membership) {
            api.getAllPerks().then(perks => ui.renderAllPerks(perks, currentUser));
            return;
        }
        api.getPerksByMembership(membership)
            .then(perks => ui.renderAllPerks(perks, currentUser))
            .catch(() => ui.showToast('Unable to filter by membership.', 'error'));
    });

    $('#filterProduct').change(function() {
        const product = $(this).val();
        if (!product) {
            api.getAllPerks().then(perks => ui.renderAllPerks(perks, currentUser));
            return;
        }
        api.getPerksByProduct(product)
            .then(perks => ui.renderAllPerks(perks, currentUser))
            .catch(() => ui.showToast('Unable to filter by product.', 'error'));
    });

    // --- Change password ---
    $('#changePasswordButton').click(() => $('#changePasswordModal').removeClass('hidden'));
    $('#cancelChangePassword').click(() => {
        $('#changePasswordModal').addClass('hidden');
        $('#changePasswordForm')[0].reset();
    });

    $('#changePasswordForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) {
            ui.showToast('Please log in first.', 'error');
            return;
        }

        const currentPassword = $('#currentPassword').val().trim();
        const newPassword = $('#newPassword').val().trim();
        const confirmPassword = $('#confirmPassword').val().trim();

        if (!currentPassword || !newPassword || !confirmPassword) {
            ui.showToast('All fields are required.', 'error');
            return;
        }
        if (newPassword !== confirmPassword) {
            ui.showToast('New password and confirmation do not match.', 'error');
            return;
        }
        if (currentPassword === newPassword) {
            ui.showToast('New password must be different.', 'error');
            return;
        }

        api.changePassword(currentUser.id, currentPassword, newPassword)
            .then(() => {
                ui.showToast('Password changed successfully! Redirecting...', 'success');
                $('#changePasswordModal').addClass('hidden');
                $('#changePasswordForm')[0].reset();
                setTimeout(() => setCurrentUser(null), 2000);
            })
            .catch(xhr => ui.showToast('Error: ' + (xhr.responseText || 'Failed to change password'), 'error'));
    });
});
