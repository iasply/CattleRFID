<!DOCTYPE html>
<html lang="<?php echo e(str_replace('_', '-', app()->getLocale())); ?>">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cattle RFID - Admin</title>
    <?php echo app('Illuminate\Foundation\Vite')(['resources/css/app.css', 'resources/js/app.js']); ?>
    <style>
        /* Modern UI Tweaks */
        :root {
            --primary: #2563eb;
            --secondary: #64748b;
            --success: #22c55e;
            --danger: #ef4444;
            --background: #f8fafc;
            --surface: #ffffff;
            --text: #1e293b;
            --sidebar: #0f172a;
        }

        body {
            background-color: var(--background);
            color: var(--text);
            font-family: 'Inter', sans-serif;
            margin: 0;
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 260px;
            background-color: var(--sidebar);
            color: white;
            padding: 2rem 1rem;
            display: flex;
            flex-direction: column;
            gap: 1.5rem;
        }

        .sidebar h1 {
            font-size: 1.5rem;
            font-weight: 800;
            margin-bottom: 2rem;
            text-align: center;
            color: #3b82f6;
        }

        .sidebar a {
            color: #cbd5e1;
            text-decoration: none;
            padding: 0.75rem 1rem;
            border-radius: 0.5rem;
            transition: all 0.2s;
            font-weight: 500;
        }

        .sidebar a:hover {
            background-color: #1e293b;
            color: white;
        }

        .sidebar a.active {
            background-color: var(--primary);
            color: white;
        }

        .main-content {
            flex: 1;
            padding: 2rem;
            overflow-y: auto;
        }

        .card {
            background: var(--surface);
            border-radius: 0.75rem;
            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
            padding: 1.5rem;
            margin-bottom: 2rem;
        }

        .btn {
            padding: 0.5rem 1rem;
            border-radius: 0.375rem;
            font-weight: 600;
            cursor: pointer;
            border: none;
            transition: opacity 0.2s;
        }

        .btn-primary {
            background-color: var(--primary);
            color: white;
        }

        .btn-success {
            background-color: var(--success);
            color: white;
        }

        .btn-danger {
            background-color: var(--danger);
            color: white;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }

        th {
            text-align: left;
            padding: 1rem;
            border-bottom: 2px solid #e2e8f0;
            color: var(--secondary);
        }

        td {
            padding: 1rem;
            border-bottom: 1px solid #e2e8f0;
        }

        form div {
            margin-bottom: 1rem;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }

        input {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #cbd5e1;
            border-radius: 0.375rem;
        }

        .alert {
            padding: 1rem;
            border-radius: 0.5rem;
            margin-bottom: 1rem;
        }

        .alert-success {
            background-color: #dcfce7;
            color: #166534;
        }
    </style>
</head>

<body>
    <div class="sidebar">
        <h1>🐂 Cattle RFID</h1>
        <a href="<?php echo e(route('admin.dashboard')); ?>"
            class="<?php echo e(request()->routeIs('admin.dashboard') ? 'active' : ''); ?>">Dashboard</a>
        <a href="<?php echo e(route('admin.veterinarians.index')); ?>"
            class="<?php echo e(request()->routeIs('admin.veterinarians.*') ? 'active' : ''); ?>">Veterinários</a>
        <a href="<?php echo e(route('admin.cattle.index')); ?>"
            class="<?php echo e(request()->routeIs('admin.cattle.*') ? 'active' : ''); ?>">Animais</a>
        <a href="<?php echo e(route('admin.vaccines.index')); ?>"
            class="<?php echo e(request()->routeIs('admin.vaccines.*') ? 'active' : ''); ?>">Vacinas</a>
        <a href="<?php echo e(route('admin.workstations.index')); ?>"
            class="<?php echo e(request()->routeIs('admin.workstations.*') ? 'active' : ''); ?>">Estações</a>

        <div style="margin-top: auto;">
            <form action="<?php echo e(route('logout')); ?>" method="POST">
                <?php echo csrf_field(); ?>
                <button type="submit" class="btn btn-danger" style="width: 100%;">Sair</button>
            </form>
        </div>
    </div>

    <main class="main-content">
        <?php if(session('success')): ?>
            <div class="alert alert-success">
                <?php echo e(session('success')); ?>

            </div>
        <?php endif; ?>

        <?php echo $__env->yieldContent('content'); ?>
    </main>
</body>

</html><?php /**PATH D:\arduinorfid\WEB\resources\views/layouts/app.blade.php ENDPATH**/ ?>