#!/bin/bash

echo "Starting test suite..."

echo "=================================================="
echo "1. Running PHP web module tests..."
echo "=================================================="
cd modulo_web || exit 1
php artisan test
if [ $? -ne 0 ]; then
  echo "PHP tests failed! Aborting."
  exit 1
fi

echo ""
echo "=================================================="
echo "2. Starting Laravel development server in the background..."
echo "=================================================="
# Start the server and redirect output so it doesn't clutter the test output
php artisan serve > /dev/null 2>&1 &
SERVER_PID=$!

# Wait a few seconds for the server to fully initialize
echo "Waiting for server to start..."
sleep 3

echo ""
echo "=================================================="
echo "3. Running Java desktop module tests..."
echo "=================================================="
cd ../modulo_desktop || { kill $SERVER_PID; exit 1; }
mvn test
JAVA_TEST_RESULT=$?

echo ""
echo "=================================================="
echo "4. Stopping Laravel development server..."
echo "=================================================="
# Try to gracefully kill the PHP server process
kill $SERVER_PID 2>/dev/null

echo ""
if [ $JAVA_TEST_RESULT -ne 0 ]; then
  echo "❌ Java tests failed!"
  exit 1
fi

echo "✅ All tests completed successfully!"
