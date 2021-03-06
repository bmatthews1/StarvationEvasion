package starvationevasion.server.io.formatters;

import starvationevasion.server.io.NotImplementedException;
import starvationevasion.server.io.strategies.SecureStream;
import starvationevasion.server.model.Response;
import starvationevasion.server.model.Sendable;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class HTMLFormat extends Format<Sendable, String>
{

  public HTMLFormat (SecureStream encryption)
  {
    super(encryption);
  }

  @Override
  public String convert (Sendable data) throws IOException
  {

    FileInputStream fileIn = null;
    byte[] data1 = new byte[0];
    boolean filenotfound = false;
    try
    {
      fileIn = new FileInputStream("data/server/"+ ((Response) data).getView());
    }
    catch(Exception e)
    {
      e.printStackTrace();
      filenotfound = true;
    }

    if (!filenotfound)
    {
      byte[] bytes = new byte[1024];
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      int length;
      while((length = fileIn.read(bytes)) != -1)
      {
        bos.write(bytes, 0, length);

      }
      bos.flush();
      bos.close();
      data1 = bos.toByteArray();
      fileIn.close();
    }

    String dataStr = new String(data1, "UTF-8");
    return dataStr;
  }

  @Override
  public byte[] convertToBytes (String data)
  {
    throw new NotImplementedException();
  }

  @Override
  public String toString ()
  {
    return "text/html; charset=UTF-8";
  }

  @Override
  public String convertWithInjection (Sendable data, String i) throws IOException
  {
    String convertedDoc = this.convert(data);
    convertedDoc = convertedDoc.replace("{{data}}", i);
    return convertedDoc;
  }
}
