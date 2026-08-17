package factorymethod;

public class WhatsAppNotification implements Notification{
  @Override
  public void send(String message) {
    System.out.println("sending whatsapp notification : "+ message );
  }
}
