<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html class="light" lang="en">
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <title>LuxeStay Hotel Information</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <script id="tailwind-config">
        tailwind.config = {
            darkMode: "class",
            theme: {
                extend: {
                    "colors": {
                        "inverse-on-surface": "#f1f0f7",
                        "surface-container-lowest": "#ffffff",
                        "on-primary-fixed-variant": "#264191",
                        "primary-container": "#1e3a8a",
                        "on-background": "#1a1b21",
                        "surface-tint": "#4059aa",
                        "primary-fixed-dim": "#b6c4ff",
                        "on-error": "#ffffff",
                        "background": "#faf8ff",
                        "inverse-surface": "#2f3036",
                        "surface-container-highest": "#e3e1e9",
                        "surface-container": "#eeedf4",
                        "on-tertiary-container": "#4e3d00",
                        "surface-dim": "#dad9e1",
                        "primary-fixed": "#dce1ff",
                        "tertiary-container": "#cca730",
                        "primary": "#00236f",
                        "on-secondary": "#ffffff",
                        "outline-variant": "#c5c5d3",
                        "secondary": "#516072",
                        "secondary-fixed-dim": "#b9c8de",
                        "outline": "#757682",
                        "surface-container-low": "#f4f3fa",
                        "on-primary": "#ffffff",
                        "on-surface-variant": "#444651",
                        "on-tertiary": "#ffffff",
                        "error": "#ba1a1a",
                        "inverse-primary": "#b6c4ff",
                        "tertiary-fixed-dim": "#e9c349",
                        "surface-variant": "#e3e1e9",
                        "secondary-fixed": "#d4e4fa",
                        "on-secondary-fixed-variant": "#39485a",
                        "on-surface": "#1a1b21",
                        "on-secondary-fixed": "#0d1c2d",
                        "tertiary": "#735c00",
                        "on-primary-fixed": "#00164e",
                        "on-secondary-container": "#556477",
                        "surface-bright": "#faf8ff",
                        "on-tertiary-fixed": "#241a00",
                        "on-primary-container": "#90a8ff",
                        "secondary-container": "#d2e1f7",
                        "on-tertiary-fixed-variant": "#574500",
                        "on-error-container": "#93000a",
                        "surface-container-high": "#e9e7ef",
                        "error-container": "#ffdad6",
                        "surface": "#faf8ff",
                        "tertiary-fixed": "#ffe088"
                    },
                    "borderRadius": {
                        "DEFAULT": "0.25rem",
                        "lg": "0.5rem",
                        "xl": "0.75rem",
                        "full": "9999px"
                    },
                    "spacing": {
                        "max-width": "1440px",
                        "md": "1rem",
                        "margin": "2rem",
                        "base": "4px",
                        "lg": "1.5rem",
                        "gutter": "1.5rem",
                        "xl": "2rem",
                        "xs": "0.5rem",
                        "sm": "0.75rem"
                    },
                    "fontFamily": {
                        "caption": ["Inter"],
                        "body-lg": ["Inter"],
                        "headline-md": ["Inter"],
                        "body-md": ["Inter"],
                        "label-md": ["Inter"],
                        "display-lg": ["Inter"],
                        "title-md": ["Inter"],
                        "title-lg": ["Inter"],
                        "headline-lg": ["Inter"]
                    },
                    "fontSize": {
                        "caption": ["12px", { "lineHeight": "1.4", "fontWeight": "400" }],
                        "body-lg": ["16px", { "lineHeight": "1.6", "fontWeight": "400" }],
                        "headline-md": ["24px", { "lineHeight": "1.3", "fontWeight": "600" }],
                        "body-md": ["14px", { "lineHeight": "1.5", "fontWeight": "400" }],
                        "label-md": ["12px", { "lineHeight": "1.2", "letterSpacing": "0.05em", "fontWeight": "500" }],
                        "display-lg": ["48px", { "lineHeight": "1.2", "letterSpacing": "-0.02em", "fontWeight": "700" }],
                        "title-md": ["16px", { "lineHeight": "1.4", "fontWeight": "600" }],
                        "title-lg": ["20px", { "lineHeight": "1.4", "fontWeight": "600" }],
                        "headline-lg": ["32px", { "lineHeight": "1.25", "letterSpacing": "-0.01em", "fontWeight": "600" }]
                    }
                }
            }
        }
    </script>
    <style>
        body {
            background-color: theme('colors.surface');
            color: theme('colors.on-surface');
            font-family: 'Inter', sans-serif;
            -webkit-font-smoothing: antialiased;
        }
        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
        }
    </style>
</head>
<body class="bg-surface text-on-surface font-body-md pt-16 min-h-screen flex flex-col">

<!-- TopNavBar Component - Full Width Across Screen -->
<nav class="bg-surface/95 backdrop-blur-md dark:bg-inverse-surface/95 fixed top-0 left-0 w-full z-50 border-b border-outline-variant/40 shadow-sm">
    <div class="w-full flex justify-between items-center px-6 md:px-12 h-16">
        <div class="flex items-center gap-md">
            <a href="${pageContext.request.contextPath}/" class="text-title-lg font-title-lg font-bold text-primary dark:text-primary-fixed-dim tracking-tight">LuxeStay HMS</a>
        </div>
        <div class="flex items-center gap-sm">
            <a href="${pageContext.request.contextPath}/rooms" class="hidden sm:inline-flex items-center px-3 py-1.5 text-body-md font-bold text-primary hover:text-primary-container transition-colors">Find Rooms</a>
            <button class="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-highest transition-colors text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed" title="Notifications">
                <span class="material-symbols-outlined" data-icon="notifications">notifications</span>
            </button>
            <button class="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-highest transition-colors text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed" title="Help">
                <span class="material-symbols-outlined" data-icon="help_outline">help_outline</span>
            </button>
            <a href="${pageContext.request.contextPath}/login" class="p-2 rounded-full hover:bg-surface-container-low dark:hover:bg-surface-container-highest transition-colors text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed flex items-center justify-center" title="Account / Sign in">
                <span class="material-symbols-outlined" data-icon="account_circle">account_circle</span>
            </a>
        </div>
    </div>
</nav>

<!-- Main Content - Full Width Edge-to-Edge with consistent horizontal padding -->
<main class="flex-grow w-full px-6 md:px-12 pb-xl">
    <!-- Hero Section -->
    <section class="relative w-full h-[614px] min-h-[400px] mb-xl rounded-2xl overflow-hidden shadow-sm mt-4">
        <img alt="LuxeStay Hotel Lobby" class="absolute inset-0 w-full h-full object-cover" data-alt="A grand, ultra-luxurious modern hotel lobby. High ceilings with a striking contemporary chandelier. Polished marble floors reflecting soft, warm, inviting light. A sleek, minimalist reception desk carved from dark stone in the background. Plush seating areas with velvet sofas in deep navy and gold accents. The atmosphere is sophisticated, calm, and exclusive, fitting a premium corporate aesthetic." src="https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1920&q=80">
        <div class="absolute inset-0 bg-gradient-to-t from-inverse-surface/80 via-inverse-surface/40 to-transparent"></div>
        <div class="absolute bottom-0 left-0 p-xl w-full">
            <div class="max-w-4xl">
                <h1 class="text-display-lg font-display-lg text-surface-container-lowest mb-sm drop-shadow-md">LuxeStay Grand Resort</h1>
                <p class="text-title-lg font-title-lg text-surface-container-lowest opacity-90 drop-shadow">Experience unparalleled luxury and meticulous service.</p>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/rooms" class="absolute bottom-xl right-xl bg-primary text-on-primary font-title-md text-title-md px-lg py-sm rounded-xl shadow-md hover:bg-primary-container transition-colors shadow-lg shadow-primary/20 flex items-center justify-center">Book Now</a>
    </section>

    <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter">
        <!-- Left Column: Intro & Gallery -->
        <div class="col-span-1 md:col-span-8 flex flex-col gap-xl">
            <!-- Introduction -->
            <section class="bg-surface-container-lowest rounded-2xl p-lg md:p-xl border border-outline-variant shadow-[0_4px_6px_-1px_rgb(0,0,0,0.05)]">
                <h2 class="text-headline-lg font-headline-lg text-on-surface mb-md">Our Heritage &amp; Mission</h2>
                <div class="text-body-lg font-body-lg text-on-surface-variant space-y-md">
                    <p class="">Founded on the principles of discreet elegance and uncompromising quality, LuxeStay has been a sanctuary for discerning travelers for over two decades. We believe that true luxury lies in the anticipation of needs and the seamless execution of service.</p>
                    <p class="">Our mission is to provide an environment where operational efficiency effortlessly supports an atmosphere of calm and comfort. Every detail, from the ambient lighting in our corridors to the precise density of our linens, is curated to ensure your stay is not just restful, but restorative.</p>
                </div>
            </section>

            <!-- Photo Gallery (Bento Grid) -->
            <section>
                <h2 class="text-headline-lg font-headline-lg text-on-surface mb-md">Discover LuxeStay</h2>
                <div class="grid grid-cols-2 md:grid-cols-3 gap-sm md:gap-md h-[440px]">
                    <div class="col-span-2 row-span-2 rounded-2xl overflow-hidden border border-outline-variant shadow-sm">
                        <img class="w-full h-full object-cover hover:scale-105 transition-transform duration-500" data-alt="A spacious, high-end hotel suite. Floor-to-ceiling windows offering a panoramic city view at dusk. A plush king-sized bed with pristine white linens. A modern sitting area with a slate-grey sofa and a glass coffee table. Subtle, warm lighting highlighting architectural details. Corporate modern aesthetic, emphasizing clean lines and high-quality materials." src="https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80">
                    </div>
                    <div class="rounded-2xl overflow-hidden border border-outline-variant shadow-sm">
                        <img class="w-full h-full object-cover hover:scale-105 transition-transform duration-500" data-alt="A serene, luxurious spa room. Soft, diffused lighting creating a calming ambiance. A massage table covered in crisp white towels. Bamboo accents and smooth river stones decorating the space. A subtle hint of steam in the air. The mood is tranquil, restorative, and sophisticated." src="https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=600&q=80">
                    </div>
                    <div class="rounded-2xl overflow-hidden border border-outline-variant shadow-sm">
                        <img class="w-full h-full object-cover hover:scale-105 transition-transform duration-500" data-alt="An elegant fine dining restaurant within a luxury hotel. Tables set with gleaming silverware, crystal glasses, and crisp white tablecloths. A sleek, modern bar in the background. Soft, intimate lighting casting warm glows. Deep navy and gold design accents throughout the room." src="https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=600&q=80">
                    </div>
                </div>
            </section>

            <!-- Amenities -->
            <section class="bg-surface-container-lowest rounded-2xl p-lg md:p-xl border border-outline-variant shadow-[0_4px_6px_-1px_rgb(0,0,0,0.05)]">
                <h2 class="text-headline-md font-headline-md text-on-surface mb-lg">Premium Amenities</h2>
                <div class="flex flex-wrap gap-md">
                    <div class="flex items-center gap-sm bg-surface-container-low px-md py-sm rounded-xl border border-outline-variant/50">
                        <span class="material-symbols-outlined text-primary" data-icon="wifi">wifi</span>
                        <span class="text-title-md font-title-md text-on-surface-variant">High-Speed WiFi</span>
                    </div>
                    <div class="flex items-center gap-sm bg-surface-container-low px-md py-sm rounded-xl border border-outline-variant/50">
                        <span class="material-symbols-outlined text-primary" data-icon="pool">pool</span>
                        <span class="text-title-md font-title-md text-on-surface-variant">Infinity Pool</span>
                    </div>
                    <div class="flex items-center gap-sm bg-surface-container-low px-md py-sm rounded-xl border border-outline-variant/50">
                        <span class="material-symbols-outlined text-primary" data-icon="spa">spa</span>
                        <span class="text-title-md font-title-md text-on-surface-variant">Holistic Spa</span>
                    </div>
                    <div class="flex items-center gap-sm bg-surface-container-low px-md py-sm rounded-xl border border-outline-variant/50">
                        <span class="material-symbols-outlined text-primary" data-icon="fitness_center">fitness_center</span>
                        <span class="text-title-md font-title-md text-on-surface-variant">State-of-the-Art Gym</span>
                    </div>
                    <div class="flex items-center gap-sm bg-surface-container-low px-md py-sm rounded-xl border border-outline-variant/50">
                        <span class="material-symbols-outlined text-primary" data-icon="restaurant">restaurant</span>
                        <span class="text-title-md font-title-md text-on-surface-variant">Fine Dining</span>
                    </div>
                </div>
            </section>
        </div>

        <!-- Right Column: Contact & Policies -->
        <div class="col-span-1 md:col-span-4 flex flex-col gap-xl">
            <!-- Contact & Map -->
            <section class="bg-surface-container-lowest rounded-2xl border border-outline-variant shadow-[0_4px_6px_-1px_rgb(0,0,0,0.05)] overflow-hidden flex flex-col">
                <div class="p-lg md:p-xl border-b border-outline-variant">
                    <h2 class="text-title-lg font-title-lg text-on-surface mb-md">Location &amp; Contact</h2>
                    <ul class="space-y-sm">
                        <li class="flex items-start gap-sm">
                            <span class="material-symbols-outlined text-secondary mt-1 text-[20px]" data-icon="location_on">location_on</span>
                            <span class="text-body-md font-body-md text-on-surface-variant">123 Hang Gai Street, Hoan Kiem District<br>Hanoi, Vietnam</span>
                        </li>
                        <li class="flex items-center gap-sm">
                            <span class="material-symbols-outlined text-secondary text-[20px]" data-icon="call">call</span>
                            <span class="text-body-md font-body-md text-on-surface-variant">+84 (0) 24 3828 5555</span>
                        </li>
                        <li class="flex items-center gap-sm">
                            <span class="material-symbols-outlined text-secondary text-[20px]" data-icon="mail">mail</span>
                            <span class="text-body-md font-body-md text-on-surface-variant">reservations@luxestay.com</span>
                        </li>
                    </ul>
                </div>
                <!-- Interactive Google Map - Hanoi, Vietnam -->
                <div class="h-64 w-full bg-surface-container relative overflow-hidden">
                    <iframe 
                        title="LuxeStay Resort Location Map"
                        class="w-full h-full border-0"
                        src="https://maps.google.com/maps?q=Hoan%20Kiem%20Lake,%20Hanoi,%20Vietnam&t=&z=15&ie=UTF8&iwloc=&output=embed"
                        loading="lazy"
                        referrerpolicy="no-referrer-when-downgrade"
                        allowfullscreen>
                    </iframe>
                </div>
            </section>

            <!-- Policies -->
            <section class="bg-surface-container-lowest rounded-2xl p-lg md:p-xl border border-outline-variant shadow-[0_4px_6px_-1px_rgb(0,0,0,0.05)]">
                <h2 class="text-title-lg font-title-lg text-on-surface mb-md flex items-center gap-xs">
                    <span class="material-symbols-outlined text-secondary" data-icon="policy">policy</span>
                    Hotel Policies
                </h2>
                <div class="space-y-md">
                    <div class="border-l-2 border-primary pl-md py-xs">
                        <h3 class="text-label-md font-label-md text-secondary uppercase tracking-wider mb-xs">Check-In / Check-Out</h3>
                        <p class="text-body-md font-body-md text-on-surface-variant">Check-in: 3:00 PM<br>Check-out: 11:00 AM</p>
                    </div>
                    <div class="border-l-2 border-secondary pl-md py-xs">
                        <h3 class="text-label-md font-label-md text-secondary uppercase tracking-wider mb-xs">Cancellation Policy</h3>
                        <p class="text-body-md font-body-md text-on-surface-variant">Free cancellation up to 48 hours prior to arrival. Late cancellations subject to one night's room rate.</p>
                    </div>
                    <div class="border-l-2 border-secondary pl-md py-xs">
                        <h3 class="text-label-md font-label-md text-secondary uppercase tracking-wider mb-xs">Pet Policy</h3>
                        <p class="text-body-md font-body-md text-on-surface-variant">Small pets (under 25lbs) welcome with a non-refundable $150 cleaning fee per stay.</p>
                    </div>
                </div>
            </section>
        </div>
    </div>
</main>

<!-- Footer Component - Full Width Across Screen -->
<footer class="bg-surface-container-low dark:bg-surface-dim border-t border-outline-variant w-full py-md px-6 md:px-12 flex flex-col md:flex-row justify-between items-center gap-sm mt-auto">
    <span class="text-caption font-caption text-secondary dark:text-secondary-fixed-dim">
        © 2026 LuxeStay Operational Systems. All Rights Reserved.
    </span>
    <div class="flex gap-md">
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">Privacy Policy</a>
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">Support Docs</a>
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">Contact Admin</a>
        <a class="text-body-md font-body-md text-on-surface-variant dark:text-outline-variant hover:text-primary dark:hover:text-primary-fixed transition-colors opacity-80 hover:opacity-100" href="#">System Status</a>
    </div>
</footer>

</body>
</html>
