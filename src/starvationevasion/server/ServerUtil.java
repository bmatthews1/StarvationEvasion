package starvationevasion.server;



import java.io.IOException;
import java.util.Map;

/**
 * Shea Polansky
 * Static server utility methods
 */
public class ServerUtil
{
  public static Process StartAIProcess(String[] command/*, String hostname, int port, String username, String password*/)
  {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.inheritIO();
    // final Map<String, String> environment = processBuilder.environment();
//    environment.put("SEHOSTNAME", hostname);
//    environment.put("SEPORT", "" + port);
//    environment.put("SEUSERNAME", username);
//    environment.put("SEPASSWORD", password);
    try
    {
      return processBuilder.start();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return null;
  }
}
