package itstep.social_freedom.service;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Tag;
import itstep.social_freedom.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> allTag() {
        return tagRepository.findAll();
    }

    public Tag findTagById(Long id) {
        return tagRepository.findById(id).orElse(new Tag());
    }
}
