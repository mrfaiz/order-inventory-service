create table products (
                          id uuid primary key,
                          sku varchar(64) not null,
                          name varchar(255) not null,
                          price_cents bigint not null,
                          created_at timestamptz not null,
                          constraint uk_products_sku unique (sku)
);

create table inventory_items (
                                 id uuid primary key,
                                 product_id uuid not null,
                                 quantity integer not null,
                                 updated_at timestamptz not null,
                                 constraint uk_inventory_product_id unique (product_id),
                                 constraint fk_inventory_product
                                     foreign key (product_id)
                                         references products(id)
);

create table orders (
                        id uuid primary key,
                        status varchar(32) not null,
                        created_at timestamptz not null
);

create table order_lines (
                             id uuid primary key,
                             order_id uuid not null,
                             product_id uuid not null,
                             quantity integer not null,
                             unit_price_cents bigint not null,
                             constraint fk_order_lines_order
                                 foreign key (order_id)
                                     references orders(id),
                             constraint fk_order_lines_product
                                 foreign key (product_id)
                                     references products(id)
);

create index idx_order_lines_order_id
    on order_lines(order_id);

create index idx_order_lines_product_id
    on order_lines(product_id);
