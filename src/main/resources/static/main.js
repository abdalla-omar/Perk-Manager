// Handles user interactions, form submissions, and updates the UI dynamically
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

        api.getUserPerks(currentUser.id).then(ui.renderPerks);
        api.getProfile(currentUser.id).then(m => {
            ui.renderProfile(m);
            ui.updateMembershipOptions(m);
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
    api.getAllPerks().then(ui.renderAllPerks);   // 🔹 load all perks on page load
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
            .then(user => {
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
        if (!currentUser) return alert('Please log in first.'); // logically should be unreachable, but ok to leave for now

        const perk = {
            description: $('#perkDescription').val().trim(),
            membership: $('#perkMembership').val().trim(),
            product: $('#perkProduct').val().trim(),
            startDate: $('#perkStartDate').val(),
            endDate: $('#perkEndDate').val()
        };

        api.createPerk(currentUser.id, perk).then(() => {
            this.reset();
            // refresh both your perks and the global list
            api.getUserPerks(currentUser.id).then(ui.renderPerks);
            api.getAllPerks().then(ui.renderAllPerks);
        });
    });

    // Add membership to profile
    $('#addMembershipForm').submit(function(e) {
        e.preventDefault();
        if (!currentUser) return alert('Please log in first.'); // logically should be unreachable, but ok to leave for now

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
