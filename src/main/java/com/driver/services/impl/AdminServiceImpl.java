package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin=new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin=adminRepository1.findById(adminId).get();

        //setup provider part
        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        //setup admin part
        List<ServiceProvider> serviceProviderList=admin.getServiceProviderList();
        serviceProviderList.add(serviceProvider);
        admin.setServiceProviderList(serviceProviderList);

        //saving the parent
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        countryName=countryName.toUpperCase();
        ServiceProvider serviceProvider=serviceProviderRepository1.findById(serviceProviderId).get();

        if(countryName.equals("IND") || countryName.equals("AUS") || countryName.equals("USA") || countryName.equals("CHI") || countryName.equals("JPN")){
            Country country=new Country();

            if (countryName.equals("IND")){
                country.setCountryName(CountryName.IND);
                country.setCodes(CountryName.IND.toCode());
            }
            if (countryName.equals("AUS")){
                country.setCountryName(CountryName.AUS);
                country.setCodes(CountryName.AUS.toCode());
            }
            if (countryName.equals("USA")){
                country.setCountryName(CountryName.USA);
                country.setCodes(CountryName.USA.toCode());
            }
            if (countryName.equals("CHI")){
                country.setCountryName(CountryName.CHI);
                country.setCodes(CountryName.CHI.toCode());
            }
            if (countryName.equals("JPN")){
                country.setCountryName(CountryName.JPN);
                country.setCodes(CountryName.JPN.toCode());
            }
            //setup serviceProvider
            country.setServiceProvider(serviceProvider);
            List<Country>countryList=serviceProvider.getCountryList();
            countryList.add(country);
            serviceProvider.setCountryList(countryList);
            serviceProviderRepository1.save(serviceProvider);
            return serviceProvider;
        }else {
            throw new Exception("Country not found");
        }

    }
}
