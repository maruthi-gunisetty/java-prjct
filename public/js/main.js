// Enterprise Inventory Management System Client Script

document.addEventListener('DOMContentLoaded', function () {
    // Mobile Sidebar Toggle
    const toggleBtn = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('appSidebar');

    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('show');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function (e) {
            if (window.innerWidth <= 992) {
                if (!sidebar.contains(e.target) && !toggleBtn.contains(e.target) && sidebar.classList.contains('show')) {
                    sidebar.classList.remove('show');
                }
            }
        });
    }

    // Auto dismiss flash alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-enterprise');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.4s ease';
            setTimeout(function () {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 400);
        }, 5000);
    });
});

// Confirmation helper for destructive actions (Zero hyphens, Zero emojis)
function confirmDelete(resourceName) {
    return confirm('Are you sure you want to delete this ' + resourceName + '? This action cannot be reversed.');
}

// Print report helper
function triggerPrintReport() {
    window.print();
}

