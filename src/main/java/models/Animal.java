package models;

public class Animal {

    private String name;

    private String[] attributes;

    public Animal(String name, String[] attributes){
        this.name = name;
        this.attributes = attributes;
    }
    public String getStringAtt(){
        String str = "";

        StringBuilder sb = new StringBuilder();

        for (String s : attributes) {
            sb.append(s).append(",");
        }
        str = sb.deleteCharAt(sb.length() - 1).toString();
        return str;
    }
    public String getName() {
        return name;
    }
    public String[] getAttributes() {
        return attributes;
    }
}
