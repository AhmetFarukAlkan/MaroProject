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
import org.apache.poi.ss.usermodel.*;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/home/timesheets")
@RequiredArgsConstructor
public class TimesheetController {
    //excel method

    private final UserService userService;
    private final CustomerService customerService;
    private final TimesheetService timesheetService;
    private final TaskRepository taskRepository;

    private final TimesheetRepository timesheetRepository;

    private final com.MaroTekTimesheets.MaroProject.oauth2.Controller controller;

//
    @GetMapping("")
    public String showTimesheetList(Model model, @AuthenticationPrincipal OAuth2User user) throws ParseException {
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

//
    @GetMapping("/create")
    public String addNewTimesheet(Model model, @AuthenticationPrincipal OAuth2User user){
        if (userService.isActive(user)){
            model.addAttribute("listCustomers", customerService.GetActiveCustomers());
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
//
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

//
    @PostMapping({"/create","/edit"})
    public String createActivity(TimesheetDto timesheetDto,RedirectAttributes ra,@AuthenticationPrincipal OAuth2User user) throws ParseException {
        if (timesheetDto.getTimeSheetDate().equals("") || timesheetDto.getDuration() == null
                || timesheetDto.getDesc().equals("") || timesheetDto.getLocation().equals("")){
            if (timesheetDto.getId() == null){
                ra.addFlashAttribute("error","All fields are required.");
                return "redirect:/home/timesheets/create";
            }
            else {
                ra.addFlashAttribute("error","All fields are required.");
                return "redirect:/home/timesheets/edit/" + timesheetDto.getId();
            }
        }
        else if (timesheetDto.getDuration() < 1 || timesheetDto.getDuration() > 24){
            if (timesheetDto.getId() == null){
                ra.addFlashAttribute("error","Effort must be between 1 and 24.");
                return "redirect:/home/timesheets/create";
            }
            else {
                ra.addFlashAttribute("error","Effort must be between 1 and 24.");
                return "redirect:/home/timesheets/edit/" + timesheetDto.getId();
            }
        }
        else {
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


//    @GetMapping("/export?UserValue={User}&yearValue={year}&monthValue={month}&customerValue={customer}&mail={mail}")

//    http://localhost:8080/home/timesheets/export?UserValue=ahmetfarukalkan16@gmail.com&yearValue=2022&monthValue=-1&customerValue=0&mail=ahmetfarukalkan16@gmail.com
//@PostMapping("/export")
//    public String exportToExcel(FilterDto filterDto, @AuthenticationPrincipal OAuth2User user) throws IOException {
    @GetMapping("/export/UserValue={user}&yearValue={year}&monthValue={month}&customerValue={customer}&mail={email}")
//@GetMapping("/export")

    public String exportToExcel(@PathVariable("user") String UserValue, @PathVariable("year") String yearValue, @PathVariable("month") String monthValue, @PathVariable("customer") String customerValue,@PathVariable("email") String mail, HttpServletResponse response) throws IOException{
//    public String exportToExcel()throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
             XSSFSheet sheet;


            FilterDto filterDto = new FilterDto();
            filterDto.setUserValue(UserValue);
            filterDto.setYearValue(yearValue);
            filterDto.setMonthValue(monthValue);
            filterDto.setCustomerValue(customerValue);
            User user = userService.getUserByEmail(mail);


    //        filterDto.setUserValue("ahmetfarukalkan16@gmail.com");
    //        filterDto.setYearValue("2022");
    //        filterDto.setMonthValue("-1");
    //        filterDto.setCustomerValue("0");
    //        User user = userService.getUserByEmail("ahmetfarukalkan16@gmail.com");

//            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//            HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();

            response.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            String currentDateTime = dateFormatter.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=" + userService.getUserByEmail(UserValue).getName() + "_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);
            sheet = workbook.createSheet("Timesheet");

            CellStyle style = workbook.createCellStyle();
            CellStyle style2 = workbook.createCellStyle();
            CellStyle style3 = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            XSSFFont font2 = workbook.createFont();

            Row row;

            font.setBold(true);
            font.setFontHeight(10);

            font2.setFontHeight(10);

            style.setFont(font);
            style2.setFont(font2);
            style3.setFont(font);

            List<Timesheet> timesheets = timesheetRepository.findAll();
            Collections.sort(timesheets);
            List<TimesheetDto> Dtotimesheets = new ArrayList<>();
            float totalHours = 0;
            if (user.getUser_role().getName().equals("ADMIN")) {
                for (Timesheet timesheet : timesheets) {
                    if ((filterDto.getUserValue().equals("0") || timesheet.getUser().getEmail().equals(filterDto.getUserValue())) &&
                            (filterDto.getYearValue().equals("0") || timesheet.getTimeSheetDate().getYear() + 1900 == Integer.valueOf(filterDto.getYearValue())) &&
                            (filterDto.getMonthValue().equals("-1") || timesheet.getTimeSheetDate().getMonth() == Integer.valueOf(filterDto.getMonthValue())) &&
                            (filterDto.getCustomerValue().equals("0") || timesheet.getCustomer().getName().equals(filterDto.getCustomerValue()))) {
                        Dtotimesheets.add(timesheetService.timesheetToTimesheetDto(timesheet));
                        totalHours += Float.valueOf(timesheet.getDuration());
                    }
                }

            }
            else if (user.getUser_role().getName().equals("USER")) {
                for (Timesheet timesheet : timesheets) {
                    if ((filterDto.getUserValue().equals("0") || timesheet.getUser().getEmail().equals(user.getEmail())) &&
                            (filterDto.getYearValue().equals("0") || timesheet.getTimeSheetDate().getYear() + 1900 == Integer.valueOf(filterDto.getYearValue())) &&
                            (filterDto.getMonthValue().equals("-1") || timesheet.getTimeSheetDate().getMonth() == Integer.valueOf(filterDto.getMonthValue())) &&
                            (filterDto.getCustomerValue().equals("0") || timesheet.getCustomer().getName().equals(filterDto.getCustomerValue()))
                    ) {
                        Dtotimesheets.add(timesheetService.timesheetToTimesheetDto(timesheet));
                        totalHours += Float.valueOf(timesheet.getDuration());
                    }
                }
            }

            row = sheet.createRow(0);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            createCell(row, 0, "MAROTEKNOLOJİ", style, sheet);

            row = sheet.createRow(2);
            createCell(row, 0, "User:", style3, sheet);
            createCell(row, 1, user.getName(), style2, sheet);

            row = sheet.createRow(3);
            createCell(row, 0, "Date:", style3, sheet);
            createCell(row, 1, currentDateTime, style2, sheet);

            row = sheet.createRow(4);
            createCell(row, 0, "Total Hours:", style3, sheet);
            createCell(row, 1, String.valueOf(totalHours), style2, sheet);

            row = sheet.createRow(5);
            createCell(row, 0, "Total Days:", style3, sheet);
            createCell(row, 1, String.valueOf(totalHours / 8), style2, sheet);

            row = sheet.createRow(8);
            createCell(row, 0, "Date", style, sheet);
            createCell(row, 1, "Activity", style, sheet);
            createCell(row, 2, "Project Name", style, sheet);
            createCell(row, 3, "Duration", style, sheet);
            createCell(row, 4, "Location", style, sheet);
            createCell(row, 5, "Customer", style, sheet);

        int rowCount = 9;

            style = workbook.createCellStyle();
            font = workbook.createFont();
            font.setFontHeight(10);
            style.setFont(font);

            DateFormat dateFormatter2 = new SimpleDateFormat("dd MMMM yyyy EEEEE");

        for (TimesheetDto timesheet : Dtotimesheets) {
                Row row2 = sheet.createRow(rowCount);
                rowCount++;
                int columnCount = 0;

                createCell(row2, columnCount, dateFormatter2.format(timesheet.getCreateDate()), style, sheet);
                columnCount++;
    //            System.out.println(timesheet.getCreateDate().toString());
                createCell(row2, columnCount, timesheet.getTask(), style, sheet);
                columnCount++;
    //            System.out.println(timesheet.getTask());
                createCell(row2, columnCount, timesheet.getCustomer(), style, sheet);
                columnCount++;
    //            System.out.println(timesheet.getCustomer());
                createCell(row2, columnCount, timesheet.getDuration().toString(), style, sheet);
                columnCount++;
    //            System.out.println(timesheet.getDuration());
                createCell(row2, columnCount, timesheet.getLocation(), style, sheet);
                columnCount++;
    //            System.out.println(timesheet.getLocation());
                createCell(row2, columnCount, timesheet.getCustomer(), style, sheet);
    //            System.out.println(timesheet.getCustomer());
            }

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

    //    workbook.write(response.getOutputStream());

    //    response.getOutputStream().flush();
    //    response.getOutputStream().close();

        workbook.close();

        outputStream.flush();
        outputStream.close();
            return null;
        }catch (IllegalStateException e)
        {
            System.out.println();
        }
//    response.getOutputStream().flush();
//    response.getOutputStream().close();

//    return "redirect:/home/timesheets";
    return null;

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style, XSSFSheet sheet) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
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
