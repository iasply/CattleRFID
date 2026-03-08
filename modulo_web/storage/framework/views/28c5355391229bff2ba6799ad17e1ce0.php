

<?php $__env->startSection('content'); ?>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Veterinários Cadastrados</h2>
        <a href="<?php echo e(route('admin.veterinarians.create')); ?>" class="btn btn-primary">+ Novo Veterinário</a>
    </div>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <?php $__currentLoopData = $vets; $__env->addLoop($__currentLoopData); foreach($__currentLoopData as $vet): $__env->incrementLoopIndices(); $loop = $__env->getLastLoop(); ?>
                    <tr>
                        <td><?php echo e($vet->name); ?></td>
                        <td><code><?php echo e($vet->vet_rfid); ?></code></td>
                        <td><?php echo e($vet->email); ?></td>
                        <td style="display: flex; gap: 0.5rem;">
                            <a href="<?php echo e(route('admin.veterinarians.show', $vet)); ?>" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none; background-color: #6366f1; border-color: #6366f1;">Ver</a>
                            <a href="<?php echo e(route('admin.veterinarians.edit', $vet)); ?>" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none;">Editar</a>
                        </td>
                    </tr>
                <?php endforeach; $__env->popLoop(); $loop = $__env->getLastLoop(); ?>
                <?php if($vets->isEmpty()): ?>
                    <tr>
                        <td colspan="4" style="text-align: center; color: var(--secondary);">Nenhum veterinário cadastrado.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
<?php $__env->stopSection(); ?>
<?php echo $__env->make('layouts.app', array_diff_key(get_defined_vars(), ['__data' => 1, '__path' => 1]))->render(); ?><?php /**PATH D:\arduinorfid\WEB\resources\views/admin/veterinarians/index.blade.php ENDPATH**/ ?>