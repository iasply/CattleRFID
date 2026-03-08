<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Cattle RFID</title>
    <style>
        body {
            background-color: #f1f5f9;
            font-family: sans-serif;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }

        .login-card {
            background: white;
            padding: 2.5rem;
            border-radius: 1rem;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
        }

        h2 {
            text-align: center;
            color: #1e293b;
            margin-bottom: 2rem;
        }

        .input-group {
            margin-bottom: 1.5rem;
        }

        label {
            display: block;
            margin-bottom: 0.5rem;
            color: #64748b;
            font-weight: 600;
        }

        input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #e2e8f0;
            border-radius: 0.5rem;
            box-sizing: border-box;
        }

        .btn-login {
            width: 100%;
            padding: 0.75rem;
            background-color: #2563eb;
            color: white;
            border: none;
            border-radius: 0.5rem;
            font-weight: 700;
            cursor: pointer;
            margin-top: 1rem;
        }

        .error {
            color: #ef4444;
            font-size: 0.875rem;
            margin-top: 0.5rem;
        }
    </style>
</head>

<body>
    <div class="login-card">
        <h2>🐂 Cattle RFID Admin</h2>
        <form action="{{ route('login.post') }}" method="POST">
            @csrf
            <div class="input-group">
                <label>Email</label>
                <input type="email" name="email" required value="{{ old('email') }}">
            </div>
            <div class="input-group">
                <label>Senha</label>
                <input type="password" name="password" required>
            </div>

            @if($errors->any())
                <div class="error">{{ $errors->first() }}</div>
            @endif

            <button type="submit" class="btn-login">Acessar Painel</button>
        </form>
    </div>
</body>

</html>