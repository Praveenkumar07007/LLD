package buildermethod;


import java.sql.SQLOutput;

class User {

  private final String name;
  private final String email;
  private final int age;
  private final String phone;
  private final String city;

  private User(Builder builder) {

    this.name = builder.name;
    this.email = builder.email;
    this.age = builder.age;
    this.phone = builder.phone;
    this.city = builder.city;
  }

  public String getPhone() {
    return phone;
  }

  public String getName(){
    return name;
  }

  public int getAge() {
    return age;
  }

  public String getCity() {
    return city;
  }

  public String getEmail() {
    return email;
  }


  public static class Builder{
    private String name;
    private String email;
    private int age;
    private String phone;
    private String city;

    public Builder(String name, String email) {
      this.name = name;
      this.email = email;
    }

    public Builder age(int age) {
      this.age = age;
      return this;
    }

    public Builder phone(String phone) {
      this.phone = phone;
      return this;
    }

    public Builder city(String city) {
      this.city = city;
      return this;
    }

    public User build(){
      return new User(this);
    }
  }

}

public class BuilderMethodDemo {
  public static void main(String[] args) {

    User user = new User.Builder(
            "Praveen",
            "praveen@gmail.com"
    )
            .age(25)
            .phone("9999999999")
            .city("Noida")
            .build();

    System.out.println(user.getPhone());


    User newuser = new User.Builder("jhon", "jhon@gmail.com").city("noida").build();

    System.out.println(newuser.getPhone());

  }
}
