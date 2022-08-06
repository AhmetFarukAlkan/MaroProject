package com.MaroTekTimesheets.MaroProject.Controller;

import com.MaroTekTimesheets.MaroProject.Dto.CustomerDto;
import com.MaroTekTimesheets.MaroProject.Dto.FilterDto;
import com.MaroTekTimesheets.MaroProject.Dto.TimesheetDto;
import com.MaroTekTimesheets.MaroProject.Entity.Customer;
import com.MaroTekTimesheets.MaroProject.Entity.Task;
import com.MaroTekTimesheets.MaroProject.Entity.Timesheet;
import com.MaroTekTimesheets.MaroProject.Entity.User;
import com.MaroTekTimesheets.MaroProject.Repo.CustomerRepository;
import com.MaroTekTimesheets.MaroProject.Repo.TaskRepository;
import com.MaroTekTimesheets.MaroProject.Repo.TimesheetRepository;
import com.MaroTekTimesheets.MaroProject.Service.CustomerService;
import com.MaroTekTimesheets.MaroProject.Service.TimesheetService;
import com.MaroTekTimesheets.MaroProject.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/home/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final UserService userService;
    private final CustomerService customerService;
    private final TimesheetService timesheetService;
    private final TaskRepository taskRepository;

    private final TimesheetRepository timesheetRepository;

    private final com.MaroTekTimesheets.MaroProject.oauth2.Controller controller;

    @GetMapping("")
    public String showCustomerList(Model model, @AuthenticationPrincipal OAuth2User user) throws ParseException {
        if (userService.isActive(user)){
            if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
                model.addAttribute("listTimesheets", timesheetService.GetTimeSheets());
                model.addAttribute("listCustomers", customerService.GetCustomers());
                model.addAttribute("listUsers", userService.GetUsers());
                model.addAttribute("filter", new FilterDto());
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                return "timesheets";
            }
            else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
                List<Timesheet> timesheets = timesheetRepository.findAll();
                Collections.sort(timesheets);
                List<TimesheetDto> Dtotimesheets = new ArrayList<>();
                for (Timesheet timesheet:timesheets) {
                    if (timesheet.getUser().getEmail().equals(userService.ControlUser(user).getEmail())){
                        Dtotimesheets.add(timesheetService.timesheetToTimesheetDto(timesheet));
                    }
                }
                model.addAttribute("listTimesheets", Dtotimesheets);
                model.addAttribute("listCustomers", customerService.GetCustomers());
                List<User> users = new ArrayList<>();
                users.add(userService.ControlUser(user));
                model.addAttribute("listUsers", users);
                FilterDto filterDto = new FilterDto();
                filterDto.setUserValue(userService.ControlUser(user).getEmail());
                model.addAttribute("filter", filterDto);
                model.addAttribute("picture", user.getAttributes().get("picture"));
                model.addAttribute("user", userService.ControlUser(user));
                return "timesheets";
            }
        }
        else {
            return "redirect:/passive-user";
        }
        return "redirect:/";
    }


//    @GetMapping("/get")
//    public ResponseEntity<List<Timesheet>> GetTimeSheets(){
//        return ResponseEntity.ok(timesheetRepository.findAll());
//    }

    @GetMapping("/create")
    public String addNewTimesheet(Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            model.addAttribute("listCustomers", customerService.GetCustomers());
            model.addAttribute("tasks",timesheetService.GetTasks());
            model.addAttribute("timesheet", new TimesheetDto());
            model.addAttribute("pagetitle", "Create New Activity");
            model.addAttribute("picture", user.getAttributes().get("picture"));
            model.addAttribute("user", userService.ControlUser(user));
            return "timesheet_form";
        }else {
            return "redirect:/passive-user";
        }
    }

    @GetMapping("/edit/{id}")
    public String editActivityForm(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            TimesheetDto timesheetDto = timesheetService.getActivityById(id);
            model.addAttribute("listCustomers", customerService.GetCustomers());
            model.addAttribute("tasks",timesheetService.GetTasks());
            model.addAttribute("timesheet", timesheetDto);
            model.addAttribute("pagetitle", "Edit Activity");
            model.addAttribute("picture", user.getAttributes().get("picture"));
            model.addAttribute("user", userService.ControlUser(user));
            return "timesheet_form";
        }else {
            return "redirect:/passive-user";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,@AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            Timesheet timesheet = timesheetService.getTSById(id);
            timesheetService.deleteActivity(timesheet);
            return "redirect:/home/timesheets";
        }else {
            return "redirect:/passive-user";
        }

    }


    @PostMapping({"/create","/edit"})
    public String createActivity(TimesheetDto timesheetDto,@AuthenticationPrincipal OAuth2User user) throws ParseException {
        Timesheet timesheet = new Timesheet();
        timesheet.setId(timesheetDto.getId());
        timesheet.setUser(userService.ControlUser(user));
        timesheet.setCustomer(customerService.getCustomerByName(timesheetDto.getCustomer()));
        timesheet.setTask(getTaskByName(timesheetDto.getTask()));
        timesheet.setTimeSheetDate(new SimpleDateFormat("yyyy-MM-dd").parse(timesheetDto.getTimeSheetDate()));
        timesheet.setLocation(timesheetDto.getLocation());
        timesheet.setDesc(timesheetDto.getDesc());
        timesheet.setDuration(timesheetDto.getDuration());
        timesheet.setCreateDate(new Date());
        timesheetService.CreateTimesheet(timesheet);
        return "redirect:/home/timesheets";
    }


    @PostMapping("")
    public String Filter(FilterDto filterDto, Model model, @AuthenticationPrincipal OAuth2User user){
        List<Timesheet> timesheets = timesheetRepository.findAll();
        Collections.sort(timesheets);
        List<TimesheetDto> Dtotimesheets = new ArrayList<>();
        if (userService.ControlUser(user).getUser_role().getName().equals("ADMIN")){
            for (Timesheet timesheet:timesheets) {
                if ((filterDto.getUserValue().equals("0") || timesheet.getUser().getEmail().equals(filterDto.getUserValue()))&&
                        (filterDto.getYearValue().equals("0") || timesheet.getTimeSheetDate().getYear() + 1900 == Integer.valueOf(filterDto.getYearValue()))&&
                        (filterDto.getMonthValue().equals("-1") || timesheet.getTimeSheetDate().getMonth() == Integer.valueOf(filterDto.getMonthValue()))&&
                        (filterDto.getCustomerValue().equals("0") || timesheet.getCustomer().getName().equals(filterDto.getCustomerValue()))){
                    Dtotimesheets.add(timesheetService.timesheetToTimesheetDto(timesheet));
                }
            }
            model.addAttribute("listTimesheets", Dtotimesheets);
            model.addAttribute("listCustomers", customerService.GetCustomers());
            model.addAttribute("listUsers", userService.GetUsers());
            model.addAttribute("filter", filterDto);
            model.addAttribute("picture", user.getAttributes().get("picture"));
            model.addAttribute("user", userService.ControlUser(user));
            return "timesheets";
        }
        else if (userService.ControlUser(user).getUser_role().getName().equals("USER")){
            for (Timesheet timesheet:timesheets) {
                if ((filterDto.getUserValue().equals("0") || timesheet.getUser().getEmail().equals(userService.ControlUser(user).getEmail()))&&
                        (filterDto.getYearValue().equals("0") || timesheet.getTimeSheetDate().getYear() + 1900 == Integer.valueOf(filterDto.getYearValue()))&&
                        (filterDto.getMonthValue().equals("-1") || timesheet.getTimeSheetDate().getMonth() == Integer.valueOf(filterDto.getMonthValue()))&&
                        (filterDto.getCustomerValue().equals("0") || timesheet.getCustomer().getName().equals(filterDto.getCustomerValue()))
                        ){
                    Dtotimesheets.add(timesheetService.timesheetToTimesheetDto(timesheet));
                }
            }
            model.addAttribute("listTimesheets", Dtotimesheets);
            model.addAttribute("listCustomers", customerService.GetCustomers());
            List<User> users = new ArrayList<>();
            users.add(userService.ControlUser(user));
            model.addAttribute("listUsers", users);
            model.addAttribute("filter", filterDto);
            model.addAttribute("picture", user.getAttributes().get("picture"));
            model.addAttribute("user", userService.ControlUser(user));
            return "timesheets";
        }
        model.addAttribute("user", new User());
        return "redirect:/";

    }

//    @PostMapping
//    public ResponseEntity<Timesheet> CreateTimeSheets(@RequestBody Timesheet timesheet){
//        return ResponseEntity.ok(timesheetService.CreateTimesheet(timesheet));
//    }

    private Task getTaskByName(String Name){
        return   taskRepository.findAll().stream()
                .filter(t -> t.getName().equals(Name))
                .findFirst().orElse(null);
    }
}

