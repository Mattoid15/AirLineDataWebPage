package Beans;

public class MyBean implements java.io.Serializable {
    private int count;
    private String beanName = "";

    // Constructor
    public void MyBean() { }

    // Setter for count
    public void setCount(int count) { this.count = count; }

    // Getter for count
    public int getCount() { return count; }

    // Setter for beanName
    public void setBeanName(String beanName) { this.beanName = beanName; }

    // Getter for beanName
    public String getBeanName() { return beanName; }
}
