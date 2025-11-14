// Defines functions to communicate with the backend via AJAX requests
const api = {
    getUsers: () => $.ajax({
        url: '/api/perkmanager',
        method: 'GET',
        dataType: 'json'
    }),

    createUser: (data) => $.ajax({
        url: '/api/perkmanager',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json'
    }),

    login: (data) => $.ajax({
        url: '/api/perkmanager/login',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json'
    }),

    getUserPerks: (id) => $.ajax({
        url: `/api/perkmanager/${id}/perks`,
        method: 'GET',
        dataType: 'json'
    }),

    createPerk: (id, perk) => $.ajax({
        url: `/api/perkmanager/${id}/perks`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(perk),
        dataType: 'json'
    }),

    getProfile: (id) => $.ajax({
        url: `/api/perkmanager/${id}/profile`,
        method: 'GET',
        dataType: 'json'
    }),

    addMembership: (id, membership) => $.ajax({
        url: `/api/perkmanager/${id}/profile`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ membership }),
        dataType: 'json'
    }),

    // 🔹 NEW: upvote a perk
    upvotePerk: (perkId) => $.ajax({
        url: `/api/perkmanager/perks/${perkId}/upvote`,
        method: 'POST',
        dataType: 'json'
    })
};
