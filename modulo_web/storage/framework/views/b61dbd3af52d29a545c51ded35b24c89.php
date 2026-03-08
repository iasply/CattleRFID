

<?php $__env->startSection('content'); ?>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Rebanho Cadastrado</h2>
        <a href="<?php echo e(route('admin.cattle.create')); ?>" class="btn btn-success">+ Novo Animal</a>
    </div>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Tag RFID</th>
                    <th>Nome/Apelido</th>
                    <th>Cadastrado por</th>
                    <th>Peso Atual</th>
                    <th>Data Registro</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <?php $__currentLoopData = $gattos; $__env->addLoop($__currentLoopData); foreach($__currentLoopData as $animal): $__env->incrementLoopIndices(); $loop = $__env->getLastLoop(); ?>
                    <tr>
                        <td><code><?php echo e($animal->rfid_tag); ?></code></td>
                        <td><?php echo e($animal->name); ?></td>
                        <td><?php echo e($animal->user->name ?? 'Sistema'); ?></td>
                        <td><?php echo e(number_format($animal->weight, 2, ',', '.')); ?> kg</td>
                        <td><?php echo e(\Carbon\Carbon::parse($animal->registration_date)->format('d/m/Y')); ?></td>
                        <td style="display: flex; gap: 0.5rem;">
                            <a href="<?php echo e(route('admin.cattle.show', $animal)); ?>" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none; background-color: #6366f1; border-color: #6366f1;">Ver</a>
                            <a href="<?php echo e(route('admin.cattle.edit', $animal)); ?>" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none;">Editar</a>
                        </td>
                    </tr>
                <?php endforeach; $__env->popLoop(); $loop = $__env->getLastLoop(); ?>
                <?php if($gattos->isEmpty()): ?>
                    <tr>
                        <td colspan="5" style="text-align: center; color: var(--secondary);">Nenhum animal cadastrado.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
<?php $__env->stopSection(); ?>
<?php echo $__env->make('layouts.app', array_diff_key(get_defined_vars(), ['__data' => 1, '__path' => 1]))->render(); ?><?php /**PATH D:\arduinorfid\WEB\resources\views/admin/cattle/index.blade.php ENDPATH**/ ?>