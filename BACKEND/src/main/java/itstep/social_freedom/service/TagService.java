package itstep.social_freedom.service;

import itstep.social_freedom.entity.Category;
import itstep.social_freedom.entity.Status;
import itstep.social_freedom.entity.Tag;
import itstep.social_freedom.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public List<Tag> allTag() {
        return tagRepository.findAll().stream().
                sorted(Comparator.comparing(Tag::getName)).collect(Collectors.toList());
    }

    public Tag findTagById(Long id) {
        return tagRepository.findById(id).orElse(new Tag());
    }

    public boolean save(Tag tag) {
        Tag tagDB = tagRepository.findTagsByName(tag.getName());
        if (tagDB != null)
            if (!Objects.equals(tagDB.getStatus(), tag.getStatus()))
                return false;
        tagRepository.save(tag);
        return true;
    }

    public void delete(Long id) {
        if (tagRepository.findById(id).isPresent()) {
            Tag tag = tagRepository.findById(id).orElse(new Tag());
            tag.setStatus(Status.DELETED);
            tagRepository.save(tag);
        }
    }
}
