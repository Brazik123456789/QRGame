package com.kolomin.balansir.Services;

import com.kolomin.balansir.Entities.Resource;
import com.kolomin.balansir.Repositoeirs.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    private ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public void saveOrUpdate(Resource newResource) {
        resourceRepository.save(newResource);
    }

    public boolean findUrl(String url) {
        if (resourceRepository.findUrl(url) != null){
            return true;
        } else {
            return false;
        }
    }
}
