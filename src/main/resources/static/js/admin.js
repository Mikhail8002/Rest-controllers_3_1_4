
let currentUser = null;
let allUsers = [];
let allRoles = [];


document.addEventListener('DOMContentLoaded', function() {
    loadCurrentUser();
    loadUsers();
    loadRoles();
});


function loadCurrentUser() {
    fetch('/api/user')
        .then(response => response.json())
        .then(user => {
            currentUser = user;
            document.getElementById('currentUserEmail').textContent = user.email || user.username;
            updateUserInfo();
        })
        .catch(error => console.error('Ошибка загрузки пользователя:', error));
}


function updateUserInfo() {
    if (currentUser) {
        const roleNames = currentUser.roles.map(r => r.name.replace('ROLE_', '')).join(' ');
        document.getElementById('currentUserRoles').textContent = roleNames;
    }
}

function loadUsers() {
    fetch('/api/admin/users')
        .then(response => response.json())
        .then(users => {
            allUsers = users;
            renderUsersTable(users);
        })
        .catch(error => console.error('Ошибка загрузки пользователей:', error));
}

function loadRoles() {
    fetch('/api/admin/roles')
        .then(response => response.json())
        .then(roles => {
            allRoles = roles;
        })
        .catch(error => console.error('Ошибка загрузки ролей:', error));
}

function renderUsersTable(users) {
    const tbody = document.getElementById('usersTableBody');
    tbody.innerHTML = '';

    users.forEach(user => {
        const row = document.createElement('tr');
        const rolesStr = user.roles.map(r => r.name.replace('ROLE_', '')).join(' ');

        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.firstName || ''}</td>
            <td>${user.lastName || ''}</td>
            <td>${user.age || ''}</td>
            <td>${user.email || ''}</td>
            <td>${rolesStr}</td>
            <td>
                <button class="btn btn-info btn-sm" onclick="openEditModal(${user.id})">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function openCreateModal() {
    // Очистка формы
    document.getElementById('userFormId').value = '';
    document.getElementById('userFormUsername').value = '';
    document.getElementById('userFormPassword').value = '';
    document.getElementById('userFormFirstName').value = '';
    document.getElementById('userFormLastName').value = '';
    document.getElementById('userFormAge').value = '';
    document.getElementById('userFormEmail').value = '';

    const rolesContainer = document.getElementById('userFormRoles');
    rolesContainer.innerHTML = '';
    allRoles.forEach(role => {
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.value = role.id;
        checkbox.id = `role_${role.id}`;
        checkbox.className = 'form-check-input';

        const label = document.createElement('label');
        label.className = 'form-check-label ml-2';
        label.htmlFor = `role_${role.id}`;
        label.textContent = role.name.replace('ROLE_', '');

        const div = document.createElement('div');
        div.className = 'form-check form-check-inline';
        div.appendChild(checkbox);
        div.appendChild(label);
        rolesContainer.appendChild(div);
    });

    document.getElementById('userFormTitle').textContent = 'Create User';
    document.getElementById('userFormSubmit').textContent = 'Create';
    document.getElementById('userForm').onsubmit = createUser;
    $('#userFormModal').modal('show');
}

function openEditModal(userId) {
    const user = allUsers.find(u => u.id === userId);
    if (!user) return;


    document.getElementById('userFormId').value = user.id;
    document.getElementById('userFormUsername').value = user.username;
    document.getElementById('userFormPassword').value = '';
    document.getElementById('userFormFirstName').value = user.firstName || '';
    document.getElementById('userFormLastName').value = user.lastName || '';
    document.getElementById('userFormAge').value = user.age || '';
    document.getElementById('userFormEmail').value = user.email || '';


    const rolesContainer = document.getElementById('userFormRoles');
    rolesContainer.innerHTML = '';
    allRoles.forEach(role => {
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.value = role.id;
        checkbox.id = `role_${role.id}`;
        checkbox.className = 'form-check-input';
        checkbox.checked = user.roles.some(r => r.id === role.id);

        const label = document.createElement('label');
        label.className = 'form-check-label ml-2';
        label.htmlFor = `role_${role.id}`;
        label.textContent = role.name.replace('ROLE_', '');

        const div = document.createElement('div');
        div.className = 'form-check form-check-inline';
        div.appendChild(checkbox);
        div.appendChild(label);
        rolesContainer.appendChild(div);
    });

    document.getElementById('userFormTitle').textContent = 'Edit User';
    document.getElementById('userFormSubmit').textContent = 'Update';
    document.getElementById('userForm').onsubmit = function(e) { updateUser(userId); return false; };
    $('#userFormModal').modal('show');
}


function createUser(event) {
    event.preventDefault();

    const userData = getUserFormData();

    fetch('/api/admin/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.error || 'Ошибка создания пользователя'); });
            }
            return response.json();
        })
        .then(user => {
            $('#userFormModal').modal('hide');
            loadUsers(); // Перезагружаем таблицу
            showAlert('Пользователь успешно создан', 'success');
        })
        .catch(error => {
            showAlert(error.message, 'danger');
        });
}


function updateUser(userId) {
    const userData = getUserFormData();

    fetch(`/api/admin/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.error || 'Ошибка обновления пользователя'); });
            }
            return response.json();
        })
        .then(user => {
            $('#userFormModal').modal('hide');
            loadUsers();
            showAlert('Пользователь успешно обновлен', 'success');
        })
        .catch(error => {
            showAlert(error.message, 'danger');
        });
}


function deleteUser(userId) {
    if (!confirm('Вы уверены, что хотите удалить этого пользователя?')) return;

    fetch(`/api/admin/users/${userId}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.error || 'Ошибка удаления пользователя'); });
            }
            return response.json();
        })
        .then(data => {
            loadUsers();
            showAlert('Пользователь успешно удален', 'success');
        })
        .catch(error => {
            showAlert(error.message, 'danger');
        });
}


function getUserFormData() {
    const roleCheckboxes = document.querySelectorAll('#userFormRoles input[type="checkbox"]:checked');
    const roleIds = Array.from(roleCheckboxes).map(cb => parseInt(cb.value));

    return {
        id: parseInt(document.getElementById('userFormId').value) || null,
        username: document.getElementById('userFormUsername').value,
        password: document.getElementById('userFormPassword').value,
        firstName: document.getElementById('userFormFirstName').value,
        lastName: document.getElementById('userFormLastName').value,
        age: parseInt(document.getElementById('userFormAge').value) || 0,
        email: document.getElementById('userFormEmail').value,
        roleIds: roleIds
    };
}


function showAlert(message, type = 'success') {
    const alertContainer = document.getElementById('alertContainer');
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    `;
    alertContainer.appendChild(alertDiv);


    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.parentNode.removeChild(alertDiv);
        }
    }, 5000);
}


function viewUserDetails(userId) {
    const user = allUsers.find(u => u.id === userId);
    if (!user) return;

    const detailsContainer = document.getElementById('userDetailsContainer');
    detailsContainer.innerHTML = `
        <div class="row">
            <div class="col-md-6">
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>Username:</strong> ${user.username}</p>
                <p><strong>First Name:</strong> ${user.firstName || '-'}</p>
                <p><strong>Last Name:</strong> ${user.lastName || '-'}</p>
            </div>
            <div class="col-md-6">
                <p><strong>Age:</strong> ${user.age || '-'}</p>
                <p><strong>Email:</strong> ${user.email || '-'}</p>
                <p><strong>Roles:</strong> ${user.roles.map(r => r.name.replace('ROLE_', '')).join(' ')}</p>
            </div>
        </div>
    `;
    $('#userDetailsModal').modal('show');
}