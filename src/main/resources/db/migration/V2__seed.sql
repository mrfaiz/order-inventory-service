-- Products
insert into products (id, sku, name, price_cents, created_at) values
                                                                  ('11111111-1111-1111-1111-111111111111', 'SKU-APPLE',  'Apple',  150, now()),
                                                                  ('22222222-2222-2222-2222-222222222222', 'SKU-BANANA', 'Banana', 100, now()),
                                                                  ('33333333-3333-3333-3333-333333333333', 'SKU-ORANGE', 'Orange', 120, now());

-- Inventory
insert into inventory_items (id, product_id, quantity, updated_at) values
                                                                       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 50, now()),
                                                                       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 30, now()),
                                                                       ('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', 20, now());
