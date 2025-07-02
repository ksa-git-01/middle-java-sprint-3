DO $$
DECLARE
    i INTEGER;
    post_titles TEXT[] := ARRAY[
        'Введение в Java разработку',
        'Spring Framework основы',
        'PostgreSQL для начинающих',
        'Docker и контейнеризация',
        'REST API лучшие практики',
        'Тестирование в Java',
        'Микросервисы архитектура',
        'Git версионный контроль',
        'Безопасность веб-приложений',
        'Производительность баз данных',
        'Clean Code принципы',
        'Агильная разработка',
        'DevOps практики',
        'Рефакторинг кода',
        'Паттерны проектирования',
        'Continuous Integration',
        'Monitoring и логирование',
        'Кэширование данных',
        'Асинхронное программирование',
        'Облачные технологии',
        'Машинное обучение',
        'Блокчейн технологии',
        'Frontend разработка',
        'Mobile приложения',
        'Искусственный интеллект'
    ];

    post_content TEXT;
    random_likes INTEGER;
    base_time TIMESTAMP := LOCALTIMESTAMP;
BEGIN
    FOR i IN 1..25 LOOP
        post_content := 'Это первый абзац поста номер ' || i || '. Здесь мы рассказываем об основных концепциях темы.' || E'\n\n' ||
                       'Второй абзац содержит более детальную информацию и примеры практического применения.' || E'\n\n' ||
                       'Третий абзац заключает наше повествование и подводит итоги.' || E'\n\n' ||
                       'Четвертый абзац добавляет дополнительные детали для тестирования обрезки контента на три строки.' || E'\n\n' ||
                       'Пятый абзац нужен для проверки функциональности truncateContent в нашем приложении.';

        -- Случайное количество лайков от 0 до 100
        random_likes := FLOOR(RANDOM() * 101);

        INSERT INTO post (title, content, likes, filename, created_at, updated_at)
        VALUES (
            post_titles[i],
            post_content,
            random_likes,
            'test-image-' || (i % 10 + 1) || '.jpg',
            base_time - INTERVAL '1 day' * (25 - i),
            base_time - INTERVAL '1 day' * (25 - i)
        );
    END LOOP;
END $$;
^^^ END OF SCRIPT ^^^

DO $$
DECLARE
   post_record RECORD;
   tag_record RECORD;
   i INTEGER;
   j INTEGER;
   random_tag_count INTEGER;
   random_comment_count INTEGER;
   tag_ids INTEGER[];
   selected_tag_id INTEGER;
   comment_content TEXT;
BEGIN
   INSERT INTO tag (name) VALUES
       ('Java'),
       ('Spring'),
       ('PostgreSQL'),
       ('Docker'),
       ('REST API'),
       ('Testing'),
       ('Microservices'),
       ('Git'),
       ('Security'),
       ('Performance'),
       ('Clean Code'),
       ('Agile'),
       ('DevOps'),
       ('Refactoring'),
       ('Design Patterns'),
       ('CI/CD'),
       ('Monitoring'),
       ('Caching'),
       ('Async'),
       ('Cloud')
   ON CONFLICT (name) DO NOTHING;

   SELECT ARRAY(SELECT id FROM tag ORDER BY id) INTO tag_ids;

   FOR post_record IN SELECT id FROM post ORDER BY id LOOP

       random_tag_count := FLOOR(RANDOM() * 6);
       -- добавляем рандомные теги в посты от 0 до 5
       FOR i IN 1..random_tag_count LOOP
           selected_tag_id := tag_ids[1 + FLOOR(RANDOM() * array_length(tag_ids, 1))];

           INSERT INTO post_tags (post_id, tag_id)
           VALUES (post_record.id, selected_tag_id)
           ON CONFLICT (post_id, tag_id) DO NOTHING;
       END LOOP;

       random_comment_count := FLOOR(RANDOM() * 5);
       -- добавляем от 0 до 4 псевдорандомных комментария
       FOR j IN 1..random_comment_count LOOP
           comment_content := 'Это комментарий номер ' || j || ' к посту ' || post_record.id || '. ' ||
                             CASE
                                 WHEN RANDOM() > 0.7 THEN 'Отличная статья! Спасибо за полезную информацию.'
                                 WHEN RANDOM() > 0.4 THEN 'Интересный материал, но хотелось бы больше примеров.'
                                 ELSE 'Полезный пост, буду использовать на практике.'
                             END;

           INSERT INTO comment (post_id, content, created_at, updated_at)
           VALUES (
               post_record.id,
               comment_content,
               NOW() - INTERVAL '1 hour' * FLOOR(RANDOM() * 24 * 7),
               NOW() - INTERVAL '1 hour' * FLOOR(RANDOM() * 24 * 7)
           );
       END LOOP;

   END LOOP;

END $$;
^^^ END OF SCRIPT ^^^