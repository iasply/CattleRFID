

<?php $__env->startSection('content'); ?>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Estações de Trabalho</h2>
        <a href="<?php echo e(route('admin.workstations.create')); ?>" class="btn btn-primary">+ Nova Estação</a>
    </div>

    <?php if(session('success')): ?>
        <div class="alert alert-success"
            style="margin-bottom: 1rem; padding: 1rem; background-color: #d1fae5; color: #065f46; border-radius: 0.375rem;">
            <?php echo e(session('success')); ?>

        </div>
    <?php endif; ?>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Hash</th>
                    <th>Descrição</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <?php $__currentLoopData = $workstations; $__env->addLoop($__currentLoopData); foreach($__currentLoopData as $ws): $__env->incrementLoopIndices(); $loop = $__env->getLastLoop(); ?>
                    <tr>
                        <td><code><?php echo e($ws->hash); ?></code></td>
                        <td><?php echo e($ws->desc); ?></td>
                        <td style="display: flex; gap: 0.5rem;">
                            <a href="<?php echo e(route('admin.workstations.edit', $ws)); ?>" class="btn btn-primary"
                                style="font-size: 0.75rem; text-decoration: none;">Editar</a>
                        </td>
                    </tr>
                <?php endforeach; $__env->popLoop(); $loop = $__env->getLastLoop(); ?>
                <?php if($workstations->isEmpty()): ?>
                    <tr>
                        <td colspan="3" style="text-align: center; color: var(--secondary);">Nenhuma estação cadastrada.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
<?php $__env->stopSection(); ?>
<?php echo $__env->make('layouts.app', array_diff_key(get_defined_vars(), ['__data' => 1, '__path' => 1]))->render(); ?><?php /**PATH D:\arduinorfid\WEB\resources\views/admin/workstations/index.blade.php ENDPATH**/ ?>