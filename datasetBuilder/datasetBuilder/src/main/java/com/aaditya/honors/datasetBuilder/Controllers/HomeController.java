package com.aaditya.honors.datasetBuilder.Controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/")
public class HomeController {

    public String index() {
        return "index";
    }

}
