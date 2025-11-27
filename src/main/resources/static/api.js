// Defines functions to communicate with the backend via AJAX requests
const api = {
    // ============================================
    // LEGACY API (kept for backward compatibility)
    // ============================================

    getUsers: () => $.ajax({
        url: '/api/perkmanager',
        method: 'GET',
        dataType: 'json'
    }),

    login: (data) => $.ajax({
        url: '/api/perkmanager/login',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json'
    }),

    // ============================================
    // CQRS API (Commands & Queries)
    // ============================================

    // Command: Create User
    createUser: (data) => $.ajax({
        url: '/api/cqrs/users',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        dataType: 'json'
    }),

    // Query: Get all perks (optimized read model)
    getAllPerks: () => $.ajax({
        url: '/api/cqrs/perks',
        method: 'GET',
        dataType: 'json'
    }),

    // Query: Get perks sorted by votes (new CQRS feature!)
    getPerksByVotes: () => $.ajax({
        url: '/api/cqrs/perks/by-votes',
        method: 'GET',
        dataType: 'json'
    }),

    // Query: Get personalized perks matching user profile
    getMatchingPerks: (userId) => $.ajax({
        url: `/api/cqrs/users/${userId}/matching-perks`,
        method: 'GET',
        dataType: 'json'
    }),

    // Command: Create Perk (userId now in body, not path)
    createPerk: (userId, perk) => $.ajax({
        url: '/api/cqrs/perks',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            userId: userId,
            description: perk.description,
            membership: perk.membership,
            product: perk.product,
            startDate: perk.startDate,
            endDate: perk.endDate
        }),
        dataType: 'json'
    }),

    // Add perk to user (fixed version)
    // In api object
    addPerkToUser: (userId, perkId) => {
        if (!perkId) {
            return $.Deferred().reject({
                responseText: 'Missing perkId'
            }).promise();
        }
        if (!userId) {
            return $.Deferred().reject({
                responseText: 'Missing userId'
            }).promise();
        }

        return $.ajax({
            url: `/api/cqrs/users/${userId}/perks/${perkId}`,
            method: 'POST',
            contentType: 'application/json',
            dataType: 'text',
            data: JSON.stringify({
                userId: userId,
                perkId: perkId
            }),
        });
    },

    // Query: Get user profile
    getProfile: (id) => $.ajax({
        url: `/api/cqrs/users/${id}/profile`,
        method: 'GET',
        dataType: 'json'
    }),

    // Command: Add membership
    addMembership: (id, membership) => $.ajax({
        url: `/api/cqrs/users/${id}/memberships`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ membership })
        // Note: no dataType specified - server returns plain text
    }),

    // Command: Upvote a perk (publishes event to Kafka!)
    upvotePerk: (perkId) => $.ajax({
        url: `/api/cqrs/perks/${perkId}/upvote`,
        method: 'POST',
        dataType: 'json'
    }),

    // Command: Downvote a perk (new CQRS feature!)
    downvotePerk: (perkId) => $.ajax({
        url: `/api/cqrs/perks/${perkId}/downvote`,
        method: 'POST',
        dataType: 'json'
    }),

    // Command: Change password
    changePassword: (userId, currentPassword, newPassword) => $.ajax({
        url: `/api/perkmanager/${userId}/password`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            currentPassword: currentPassword,
            newPassword: newPassword
        }),
        dataType: 'json'
    })
};
