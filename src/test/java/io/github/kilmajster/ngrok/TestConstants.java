package io.github.kilmajster.ngrok;

public interface TestConstants {
    String TEST_NGROK_PROP_ENABLED = NgrokConstants.PROP_NGROK_ENABLED + "=true";

    String TEST_NGROK_PROFILE = "test";
    String TEST_NGROK_PROFILE_WINDOWS = "test-windows";
    String TEST_NGROK_PROFILE_UNIX = "test-unix";

    String TEST_NGROK_API_URL = "http://mocked-ngrok-api-url:1234";
    String TEST_NGROK_TUNNELS_FILE_PATH = "/tunnels.json";

    String TEST_NGROK_WINDOWS_DEFAULT_DIR = "C:\\Users\\user\\.ngrok2";
    String TEST_NGROK_WINDOWS_BINARY_PATH = "C:\\Users\\user\\.ngrok2\\ngrok.exe";
    String TEST_NGROK_WINDOWS_START_COMMAND = TEST_NGROK_WINDOWS_BINARY_PATH + " http 8080";

    String TEST_NGROK_UNIX_DEFAULT_DIR = "/home/user/.ngrok2";
    String TEST_NGROK_UNIX_BINARY_PATH = "/home/user/.ngrok2/ngrok";
    String TEST_NGROK_UNIX_CHMOD_COMMAND = "chmod +x " + TEST_NGROK_UNIX_BINARY_PATH;
    String TEST_NGROK_UNIX_START_COMMAND = TEST_NGROK_UNIX_BINARY_PATH + " http 8080";
}