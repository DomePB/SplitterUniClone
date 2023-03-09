package de.hhu.ausgabenverwaltung.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null){
            if(Integer.valueOf(status.toString()) == HttpStatus.NOT_FOUND.value()){
                return "404";
            }
            if(Integer.valueOf(status.toString()) == HttpStatus.BAD_REQUEST.value()){
                return "400";
            }
            if(Integer.valueOf(status.toString()) == HttpStatus.UNAUTHORIZED.value()){
                return "401";
            }
        }
        return "error";
    }
}
