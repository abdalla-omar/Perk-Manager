$(document).ready(function() {
    const $userList = $('#userList');
    const $createUserForm = $('#createUserForm');
    const $loginForm = $('#loginForm');
    const $createPerkForm = $('#createPerkForm');

    //track the currently selected/logged-in user
    let currentUser = null;

    //fetch all users from the server
    function fetchUsers() {
        $.ajax({
            url: '/api/perkmanager',
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                renderUsers(data); //display users in the UI
            },
            error: function(xhr, status, error) {
                console.error('Error fetching users:', status, error);
            }
        });
    }

    //render a list of users in the DOM
    function renderUsers(users) {
        $userList.empty(); //clear existing list
        users.forEach(function(user) {
            const $li = $('<li>').text(`ID: ${user.id} - ${user.email}`);

            //fetch and display the current user's perks
            $li.click(function() {
                currentUser = user;
                fetchUserPerks(user.id);
            });

            $userList.append($li);
        });
    }

    //fetch perks for a specific user
    function fetchUserPerks(userId) {
        if (!userId && currentUser) userId = currentUser.id; //use currentUser if no ID provided
        if (!userId) return; //no user selected, do nothing

        $.ajax({
            url: `/api/perkmanager/${userId}/perks`,
            type: 'GET',
            dataType: 'json',
            success: function(perks) {
                renderUserPerks(perks); //display perks in the UI
            },
            error: function(xhr, status, error) {
                console.error('Error fetching user perks:', status, error);
            }
        });
    }

    //display user perks in the UI
    function renderUserPerks(perks) {
        const $userPerks = $('#userPerks');
        $userPerks.empty();
        if (!perks.length) {
            $userPerks.append($('<li>').text('No perks available.'));
            return;
        }
        perks.forEach(perk => {
            $userPerks.append($('<li>').text(`${perk.description} - ${perk.product}`));
        });
    }

    //handle new user creation
    $createUserForm.submit(function(event) {
        event.preventDefault();
        const email = $('#userEmail').val().trim();
        const password = $('#userPassword').val().trim();
        if (!email || !password) return;

        $.ajax({
            url: '/api/perkmanager',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email, password: password }),
            success: function(response) {
                $createUserForm[0].reset(); //clear the form
                fetchUsers(); //refresh the user list
                currentUser = response; //set the new user as current
                fetchUserPerks(response.id); //fetch perks for the new user
            },
            error: function(xhr, status, error) {
                console.error('Error creating user:', status, error);
            }
        });
    });

    //handle user login
    $loginForm.submit(function(event) {
        event.preventDefault();
        const email = $('#loginEmail').val().trim();
        const password = $('#loginPassword').val().trim();
        if (!email || !password) return;

        $.ajax({
            url: '/api/perkmanager/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email, password: password }),
            success: function(user) {
                currentUser = user;
                alert(`Logged in as ${user.email}`);
                fetchUserPerks(user.id); //show logged-in user's perks
            },
            error: function(xhr) {
                alert('Login failed: ' + xhr.responseText);
            }
        });
    });

    //handle creating a new perk for the logged-in user
    $createPerkForm.submit(function(event) {
        event.preventDefault();
        if (!currentUser) {
            alert('Please log in first.');
            return;
        }

        const perk = {
            description: $('#perkDescription').val().trim(),
            membership: $('#perkMembership').val().trim(),
            product: $('#perkProduct').val().trim(),
            startDate: $('#perkStartDate').val(),
            endDate: $('#perkEndDate').val()
        };

        $.ajax({
            url: `/api/perkmanager/${currentUser.id}/perks`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(perk),
            success: function(response) {
                $createPerkForm[0].reset(); //clear form after submission
                fetchUserPerks(currentUser.id); //refresh the perks list
            },
            error: function(xhr, status, error) {
                console.error('Error creating perk:', status, error);
            }
        });
    });

    //initial fetch of all users on page load
    fetchUsers();
});