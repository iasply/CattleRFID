

<?php $__env->startSection('content'); ?>
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
        <h2>Histórico de Vacinação</h2>
        <a href="<?php echo e(route('admin.vaccines.create')); ?>" class="btn btn-primary"
            style="background-color: orange; border-color: orange;">+ Registrar Vacina</a>
    </div>

    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>Data</th>
                    <th>Animal (Tag)</th>
                    <th>Vacina</th>
                    <th>Peso na Aplicação</th>
                    <th>Veterinário</th>
                </tr>
            </thead>
            <tbody>
                <?php $__currentLoopData = $vaccines; $__env->addLoop($__currentLoopData); foreach($__currentLoopData as $v): $__env->incrementLoopIndices(); $loop = $__env->getLastLoop(); ?>
                    <tr>
                        <td><?php echo e(\Carbon\Carbon::parse($v->vaccination_date)->format('d/m/Y')); ?></td>
                        <td><code><?php echo e($v->rfid_tag); ?></code></td>
                        <td><?php echo e($v->vaccine_type); ?></td>
                        <td><?php echo e(number_format($v->current_weight, 2, ',', '.')); ?> kg</td>
                        <td><?php echo e($v->user->name ?? 'Sistema'); ?></td>
                    </tr>
                <?php endforeach; $__env->popLoop(); $loop = $__env->getLastLoop(); ?>
                <?php if($vaccines->isEmpty()): ?>
                    <tr>
                        <td colspan="5" style="text-align: center; color: var(--secondary);">Nenhum registro de vacinação.</td>
                    </tr>
                <?php endif; ?>
            </tbody>
        </table>
    </div>
<?php $__env->stopSection(); ?>
<?php echo $__env->make('layouts.app', array_diff_key(get_defined_vars(), ['__data' => 1, '__path' => 1]))->render(); ?><?php /**PATH D:\arduinorfid\WEB\resources\views/admin/vaccines/index.blade.php ENDPATH**/ ?>