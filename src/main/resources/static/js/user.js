// Загрузка данных пользователя
document.addEventListener('DOMContentLoaded', function() {
    fetch('/api/user')
        .then(response => response.json())
        .then(user => {
            // Обновление navbar
            document.getElementById('userEmail').textContent = user.email || user.username;
            const rolesStr = user.roles.map(r => r.name.replace('ROLE_', '')).join(' ');
            document.getElementById('userRoles').textContent = rolesStr;

            // Заполнение профиля
            const container = document.getElementById('userProfileContainer');
            container.innerHTML = `
                <div class="row">
                    <div class="col-md-6">
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
        })
        .catch(error => {
            console.error('Ошибка загрузки профиля:', error);
            document.getElementById('userProfileContainer').innerHTML = `
                <div class="alert alert-danger">Ошибка загрузки данных пользователя</div>
            `;
        });
});