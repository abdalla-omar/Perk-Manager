
// Renders data and updates the page elements dynamically
const ui = {
    renderUsers: (users) => {
        const $list = $('#userList').empty();
        if (!users.length) return $list.append('<li>No users found</li>');
        users.forEach(u => $list.append(`<li>ID: ${u.id} - ${u.email}</li>`));
    },

    renderPerks: (perks) => {
        const $list = $('#userPerks').empty();
        if (!perks.length) return $list.append('<li>No perks available.</li>');
        perks.forEach(p => $list.append(`<li>${p.description} - ${p.product} - ${p.membership}</li>`));
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
