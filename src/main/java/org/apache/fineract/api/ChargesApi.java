package org.apache.fineract.api;

import org.apache.fineract.operations.Charge;
import org.apache.fineract.operations.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ChargesApi {

    @Autowired
    private ChargeRepository chargeRepository;

    @GetMapping(path = "/charges", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Charge> retrieveAll() {
        return this.chargeRepository.findAll();
    }

    @GetMapping(path = "/charges/{chargeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Charge retrieveOne(@PathVariable("chargeId") Long chargeId, HttpServletResponse response) {
        Charge charge = chargeRepository.findOne(chargeId);
        if(charge != null) {
            return charge;
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(path = "/charges", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void create(@RequestBody Charge charge, HttpServletResponse response) {
        charge.setId(null);
        chargeRepository.saveAndFlush(charge);
    }

    @PutMapping(path = "/charges/{chargeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable("chargeId") Long chargeId, @RequestBody Charge charge, HttpServletResponse response) {
        Charge existing = chargeRepository.findOne(chargeId);
        if (existing != null) {
            charge.setId(chargeId);
            chargeRepository.saveAndFlush(charge);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/charges/{chargeId}")
    public void delete(@PathVariable("chargeId") Long chargeId, HttpServletResponse response) {
        if(chargeRepository.exists(chargeId)) {
            chargeRepository.delete(chargeId);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
