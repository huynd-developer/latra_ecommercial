// src/data/db.ts

export interface Product {
  id: number;
  type: string;
  category: string;
  name: string;
  price: number;
  oldPrice: number | null;
  colors?: string[];
  image: string;
  description?: string;
  specs?: Record<string, string>;
  gallery?: string[];
  features?: string[]; // Dùng cho bộ lọc tính năng (Case/Screen Features)
  models?: string[];   // Dùng cho bộ lọc dòng máy (nếu cần mở rộng)
}

export const filterData = {
  colors: ["#414141", "#000000", "#E4DED1", "#FFFFFF", "#FF924E", "#4A5368", "#645D71", "#F3F3F3", "linear-gradient(180deg, #000 0%, #555 100%)", "#232323", "#1F473A"],
  groups: [
    { title: "Thương hiệu", stateKey: "brands", type: "checkbox", isOpen: true, items: [{ label: "Apple", value: "Apple" }, { label: "Samsung", value: "Samsung" }, { label: "Xiaomi", value: "Xiaomi" }] },
    { title: "Màu sắc", type: "color", isOpen: true },
    { title: "Bộ nhớ trong", stateKey: "storage", type: "checkbox", isOpen: true, items: [{ label: "128GB", value: "128GB" }, { label: "256GB", value: "256GB" }] }
  ]
};

export const productsDB: Product[] = [
  // --- ĐIỆN THOẠI GỐC ---
  { 
    id: 1, type: "Phone", category: "Apple", name: "iPhone 17 Pro Max 256GB", 
    price: 1199.00, oldPrice: 1299.00, 
    colors: ["#333333", "#ffffff", "#E4DED1", "#4A5368"], 
    image: "https://placehold.co/400x400/F1F1F1/333?text=iPhone+17+Pro+Max",
    description: "Chiếc điện thoại mạnh mẽ nhất với màn hình ProMotion 6.9 inch.",
    specs: { screen: "6.9 inch", chip: "A19 Bionic", battery: "29 hours" },
    gallery: ["https://placehold.co/600x600/F1F1F1/333?text=Front", "https://placehold.co/600x600/F1F1F1/333?text=Back"]
  },
  { 
    id: 2, type: "Phone", category: "Samsung", name: "Samsung Galaxy S24 Ultra 512GB", 
    price: 1299.00, oldPrice: 1399.00, 
    colors: ["#333333", "#000000", "#E4DED1", "#ffffff", "#645D71"], 
    image: "https://placehold.co/400x400/F1F1F1/333?text=Galaxy+S24+Ultra",
    description: "Quyền Năng AI Đột Phá cùng bút S-Pen.",
    specs: { screen: "6.8 inch", chip: "Snapdragon 8 Gen 3", battery: "5000mAh" },
    gallery: ["https://placehold.co/600x600/F1F1F1/333?text=S24+Front"]
  },
  { 
    id: 3, type: "Phone", category: "Apple", name: "iPhone 17 Pro 128GB", 
    price: 999.00, oldPrice: 1099.00, 
    colors: ["#333333", "#ffffff", "#E4DED1"], 
    image: "https://placehold.co/400x400/F1F1F1/333?text=iPhone+17+Pro",
    description: "Hiệu năng đỉnh cao trong kích thước nhỏ gọn.",
    specs: { screen: "6.3 inch", chip: "A19 Bionic", battery: "23 hours" },
    gallery: ["https://placehold.co/600x600/F1F1F1/333?text=17Pro+Front"]
  },
  { 
    id: 4, type: "Case", category: "Apple", name: "Super Thin iPhone 17 Pro Max Case", 
    price: 35.00, oldPrice: null, 
    colors: ["#000000", "#ffffff", "#333333"], 
    image: "https://placehold.co/400x400/222/fff?text=Super+Thin+Case",
    description: "Ốp lưng siêu mỏng, giữ nguyên thiết kế máy.",
    specs: { thickness: "0.35mm", material: "Nhựa PP" },
    gallery: ["https://placehold.co/600x600/222/fff?text=Case+Front"]
  },

  // --- CASES (ỐP LƯNG) - Đã fix ID và thêm Features/Colors ---
  { 
    id: 5, type: "Super Thin Case", category: "iPhone 17 Pro Max", name: "iPhone 17 Pro Max Super Thin Case", 
    price: 39.00, oldPrice: null, 
    colors: ["#000000", "#ffffff", "#E4DED1", "#1d5939"],
    features: ["Thin Design", "Pocket friendly", "Easy Grip"],
    image: "https://int.buypeel.com/cdn/shop/files/peel_home_hero_desktop_1.jpg?v=1725916053&width=600" 
  },
  { 
    id: 6, type: "Magnetic Case", category: "Galaxy S24 Ultra", name: "Galaxy S24 Ultra Magnetic Case", 
    price: 45.00, oldPrice: 55.00, 
    colors: ["#333333", "#4A5368", "#000000"],
    features: ["Magsafe-Compatible", "Snug fit", "Drop-Tested"],
    image: "https://int.buypeel.com/cdn/shop/files/Collection_Apple_WatchCase_83907797-29be-410a-b850-93a830239276.jpg?v=1734633765&width=600" 
  },
  
  // --- SCREEN PROTECTORS (KÍNH CƯỜNG LỰC) - Đã fix ID và thêm Features ---
  { 
    id: 7, type: "Glass Screen Protector", category: "iPhone 16 Pro Max", name: "iPhone 16 Pro Max Glass Screen Protector", 
    price: 29.00, oldPrice: 35.00, 
    features: ["9H Hardness Rating", "HD Clarity", "Oleophobic Coating"],
    image: "https://int.buypeel.com/cdn/shop/files/i16_Glass_Features.png?v=1743691200&width=600" 
  },
  { 
    id: 8, type: "Privacy Glass Screen Protector", category: "iPhone 16 Pro", name: "iPhone 16 Pro Privacy Protector", 
    price: 39.00, oldPrice: 60.00, 
    features: ["28° Privacy Filtering", "Anti-Fingerprint", "3D Design"],
    image: "https://int.buypeel.com/cdn/shop/files/callout_privacy-glassArtboard-1.png?v=1744605416&width=600" 
  },

  // --- CHARGING (SẠC) - Đã fix ID ---
  { id: 9, type: "Wireless Charger", category: "Charging", name: "Qi2 3-in-1 Folding Wireless Charging Stand", price: 79.99, oldPrice: 90.00, image: "https://int.buypeel.com/cdn/shop/files/Base_Station_Hero_2_2x_8eb14032-17a4-44df-b4d6-84e1fc3f59fa.jpg?v=1730999516&width=600" },
  { id: 10, type: "Car Charger", category: "Charging", name: "Magnetic Wireless Car Charging Mount", price: 44.00, oldPrice: 49.00, image: "https://int.buypeel.com/cdn/shop/files/Collection_Apple_WatchCase_83907797-29be-410a-b850-93a830239276.jpg?v=1734633765&width=600" },

  // --- ACCESSORIES (PHỤ KIỆN) - Đã fix ID ---
  { id: 11, type: "Monitor", category: "Accessories", name: "16\" Portable Monitor", price: 275.00, oldPrice: 375.00, image: "https://placehold.co/600x600/f1f1f1/333?text=Monitor" },
  { id: 12, type: "Backpack", category: "Accessories", name: "Terra Recycled 16\" Laptop Backpack", price: 45.00, oldPrice: 63.99, image: "https://placehold.co/600x600/f1f1f1/333?text=Backpack" }
];

export const comparisonsData = [
  { name: "iPhone 17 Pro Max", desc1: "Đỉnh Cao Công Nghệ", desc2: "Màn hình 6.9 inch", btnText: "Mua iPhone 17 Pro Max", performance: 95, camera: 95, battery: 90, reasons: ["Yêu thích hệ sinh thái Apple", "Cần camera quay video", "Thích màn hình cực lớn"], img: "https://placehold.co/200x400/transparent/333?text=17+Pro+Max" },
  { name: "Galaxy S24 Ultra", desc1: "Quyền Năng AI Đột Phá", desc2: "Tích hợp bút S-Pen", btnText: "Mua Galaxy S24 Ultra", performance: 90, camera: 98, battery: 85, reasons: ["Thích tính năng Galaxy AI", "Thường xuyên ghi chú", "Chụp ảnh zoom 10x"], img: "https://placehold.co/200x400/transparent/333?text=S24+Ultra" }
];

export const navigationData = {
  mainMenu: [
    { name: "iPhone", slug: "iphone", subItems: ["iPhone 17 Series", "iPhone 16 Series", "Cases"] },
    { name: "Samsung", slug: "samsung", subItems: ["Galaxy S24 Series", "Galaxy Z Fold6", "Cases"] },
    { name: "Accessories", slug: "accessories", subItems: ["MagSafe", "Chargers", "Cables"] }
  ],
  footerLinks: [
    { title: "Shop", links: ["All Products", "Best Sellers", "New Arrivals"] },
    { title: "Support", links: ["Help Center", "Shipping", "Returns", "Contact"] },
    { title: "Company", links: ["About Us", "Sustainability", "Terms of Service"] }
  ]
};

export const homeData = {
  heroBanners: [
    { id: 1, title: "The iPhone 17 Pro Case", subtitle: "Super Thin. Branded-Free. Minimalist.", image: "https://placehold.co/1920x800/222/fff?text=iPhone+17+Pro+Banner", btnText: "Shop Now", link: "/product/super-thin-iphone-17-pro" },
    { id: 2, title: "Galaxy S24 Ultra Series", subtitle: "Experience AI with the thinnest protection.", image: "https://placehold.co/1920x800/333/fff?text=Galaxy+S24+Banner", btnText: "Explore More", link: "/category/samsung" }
  ]
};

export const reviewsData = [
  { id: 1, userName: "Khách hàng 1", rating: 5, date: "2026-03-20", comment: "Ốp lưng cực mỏng, giao hàng nhanh!", productName: "Super Thin iPhone 17 Pro Case" },
  { id: 2, userName: "Khách hàng 2", rating: 4, date: "2026-03-22", comment: "Điện thoại xài mượt, đóng gói kỹ.", productName: "iPhone 17 Pro Max" }
];

export const cartInitialState = {
  items: [
    { cartId: "c1", productId: 1, name: "iPhone 17 Pro Max", variant: "256GB / Titanium Gray", quantity: 1, price: 1199.00, image: "https://placehold.co/100x100/F1F1F1/333?text=Thumb" },
    { cartId: "c2", productId: 4, name: "Super Thin iPhone 17 Pro Max Case", variant: "Black", quantity: 2, price: 35.00, image: "https://placehold.co/100x100/222/fff?text=Case" }
  ],
  subtotal: 1269.00,
  shipping: 0.00,
  total: 1269.00
};