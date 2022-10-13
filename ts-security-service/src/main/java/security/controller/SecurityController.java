package security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import security.domain.*;
import security.domain.CheckResult;
import security.domain.CheckInfo;
import security.service.SecurityService;

@RestController
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String home(){
        return "welcome to [Security Service]";
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/securityConfig/findAll", method = RequestMethod.GET)
    public GetAllSecurityConfigResult findAllSecurityConfig(){
        System.out.println("[Security Service][Find All]");
        return securityService.findAllSecurityConfig();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/securityConfig/create", method = RequestMethod.POST)
    public CreateSecurityConfigResult create(@RequestBody CreateSecurityConfigInfo info){
        System.out.println("[Security Service][Create] Name:" + info.getName());
        return securityService.addNewSecurityConfig(info);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/securityConfig/update", method = RequestMethod.POST)
    public UpdateSecurityConfigResult update(@RequestBody UpdateSecurityConfigInfo info){
        System.out.println("[Security Service][Update] Name:" + info.getName());
        return securityService.modifySecurityConfig(info);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/securityConfig/delete", method = RequestMethod.POST)
    public DeleteConfigResult delete(@RequestBody DeleteConfigInfo info){
        System.out.println("[Security Service][Delete] Id:" + info.getId());
        return securityService.deleteSecurityConfig(info);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/security/check", method = RequestMethod.POST)
    public CheckResult check(@RequestBody CheckInfo info){
        System.out.println("[Security Service][Check Security] Check Account Id:" + info.getAccountId());
        return securityService.check(info);
    }

    @RequestMapping(path = "/security/callInsidePayment", method = RequestMethod.POST)
    public boolean callInsidePayment(@RequestBody CallInsidePaymentInfo info){

        return securityService.callInsidePayment(info);
    }
}
