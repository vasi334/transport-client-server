package org.example;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Serializable {

    private String id;
    private String email;
    private String first_name;
    private String last_name;
    private String password;
    private String phone_number;

    public Employee(String email, String first_name, String last_name, String password, String phone_number) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.phone_number = phone_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) && Objects.equals(email, employee.email) &&
                Objects.equals(first_name, employee.first_name) && Objects.equals(last_name, employee.last_name) &&
                Objects.equals(password, employee.password) && Objects.equals(phone_number, employee.phone_number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, first_name, last_name, password, phone_number);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }
}
