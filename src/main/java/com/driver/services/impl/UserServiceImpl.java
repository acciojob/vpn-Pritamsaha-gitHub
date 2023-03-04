package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user =new User();
        user.setUsername(username);
        user.setPassword(password);

        //Country country=null;
        countryName=countryName.toUpperCase();

        if (countryName.equals("IND") || countryName.equals("AUS") || countryName.equals("USA") || countryName.equals("CHI") || countryName.equals("JPN")){
            userRepository3.save(user);
            Country country=new Country();


            if(countryName.equals("IND")){
                country.setCountryName(CountryName.IND);
                country.setCodes(CountryName.IND.toCode());
                String setOriginal=CountryName.IND.toCode()+"."+user.getId();
                user.setOriginalIp(setOriginal);
            }
            if (countryName.equals("AUS")){
                country.setCountryName(CountryName.AUS);
                country.setCodes(CountryName.AUS.toCode());
                String setOriginal=CountryName.AUS.toCode()+"."+user.getId();
                user.setOriginalIp(setOriginal);
            }
            if (countryName.equals("USA")){
                country.setCountryName(CountryName.USA);
                country.setCodes(CountryName.USA.toCode());
                String setOriginal=CountryName.USA.toCode()+"."+user.getId();
                user.setOriginalIp(setOriginal);
            }
            if (countryName.equals("CHI")){
                country.setCountryName(CountryName.CHI);
                country.setCodes(CountryName.CHI.toCode());
                String setOriginal=CountryName.CHI.toCode()+"."+user.getId();
                user.setOriginalIp(setOriginal);
            }
            if (countryName.equals("JPN")){
                country.setCountryName(CountryName.JPN);
                country.setCodes(CountryName.JPN.toCode());
                String setOriginal=CountryName.JPN.toCode()+"."+user.getId();
                user.setOriginalIp(setOriginal);
            }

            user.setCountry(country);
            user.setConnected(false);
            userRepository3.save(user);
            return user;
        }else {
            throw new Exception("Country not found");
        }
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user=userRepository3.findById(userId).get();
        ServiceProvider serviceProvider=serviceProviderRepository3.findById(serviceProviderId).get();

        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        serviceProviderList.add(serviceProvider);
        user.setServiceProviderList(serviceProviderList);

        List<User>userList=serviceProvider.getUserList();
        userList.add(user);
        serviceProvider.setUserList(userList);
        serviceProviderRepository3.save(serviceProvider);
        return user;
    }
}
