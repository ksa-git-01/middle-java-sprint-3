package ru.yandex.practicum.dao;

import java.util.List;
import java.util.Map;

public interface TagDao {
    Map<Long, List<String>> findTagsByPostIds(List<Long> postIds);
}
