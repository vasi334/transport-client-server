package org.example.interfaces;


import org.example.Employee;

import java.util.Optional;

public interface IEmployeeRepo extends Repository<Employee, String>{

    Optional<Employee> findEmployee(String email, String password);
}
