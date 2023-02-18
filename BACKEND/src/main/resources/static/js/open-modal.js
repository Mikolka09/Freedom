
$('#DeleteUserModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let action = "/user/delete/" + id;
    let label = "Are you sure you want to delete your account?";
    let text = "Select \"Delete\" below if you are ready to remove your account"
    let modal = $(this);
    modal.find('#deleteModalLabel').text(label);
    modal.find('#text').text(text);
    modal.find('#action').prop("href", action);
})

$('#AlertModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let name = button.data('name');
    let alert = button.data('text');
    let date = button.data('date');
    let actionConfirm = "/user/alerts/confirm/" + id;
    let actionDeny = "/user/alerts/deny/" + id;
    let modal = $(this);
    modal.find('#alertModalLabel').text(name);
    modal.find('#text').text(alert);
    modal.find('#date-alert').text(date);
    modal.find('#actionConfirm').prop("href", actionConfirm);
    modal.find('#actionDeny').prop("href", actionDeny);
})