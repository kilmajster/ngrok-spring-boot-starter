package com.createam.ngrok.experimental.web;

import com.createam.ngrok.service.NgrokApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import static com.createam.ngrok.experimental.web.NgrokDashboard.URL_NGROK_DASHBOARD;
import static com.createam.ngrok.service.NgrokApiClient.NGROK_URL_API_TUNNELS;

@ConditionalOnProperty(value = "ngrok.experimental.dashboard", havingValue = "enable")
@Controller
@RequestMapping(URL_NGROK_DASHBOARD)
public class NgrokDashboard {

    public static final String URL_NGROK_DASHBOARD = "/ngrok";

    private static final Logger log = LoggerFactory.getLogger(NgrokDashboard.class);

    @Autowired
    private NgrokApiClient ngrokApiClient;

    public NgrokDashboard() {
        log.warn("Ngrok experimental features are enabled!");
    }

    @GetMapping
    public String dashboard(final Map<String, Object> model, final HttpServletRequest request) {
        model.put("message", " Welcome to ngrok web panel!");
        model.put("time", new Date());

        String tunnelsAsJson = ngrokApiClient.resourceAsString(NGROK_URL_API_TUNNELS);

        model.put("tunnels", tunnelsAsJson);

        return "dashboard";
    }

    @GetMapping("/status")
    public void status(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String htmlStatusPage = ngrokApiClient.fetchStatusPageAsHtmlString();


        out.print(htmlStatusPage);

    }


}
