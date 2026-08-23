---
name: HydroMetric B2B
colors:
  surface: '#f8f9ff'
  surface-dim: '#ccdbf2'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eef4ff'
  surface-container: '#e5efff'
  surface-container-high: '#dbe9ff'
  surface-container-highest: '#d4e4fa'
  on-surface: '#0d1c2d'
  on-surface-variant: '#3f484c'
  inverse-surface: '#233143'
  inverse-on-surface: '#e9f1ff'
  outline: '#6f797c'
  outline-variant: '#bfc8cc'
  surface-tint: '#13677b'
  primary: '#004655'
  on-primary: '#ffffff'
  primary-container: '#005f73'
  on-primary-container: '#91d7ee'
  inverse-primary: '#8bd1e8'
  secondary: '#2b6958'
  on-secondary: '#ffffff'
  secondary-container: '#aeedd7'
  on-secondary-container: '#306d5c'
  tertiary: '#6a5e36'
  on-tertiary: '#ffffff'
  tertiary-container: '#baaa7c'
  on-tertiary-container: '#493f1a'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#b2ebff'
  primary-fixed-dim: '#8bd1e8'
  on-primary-fixed: '#001f27'
  on-primary-fixed-variant: '#004e5f'
  secondary-fixed: '#b0efda'
  secondary-fixed-dim: '#95d3be'
  on-secondary-fixed: '#002018'
  on-secondary-fixed-variant: '#0b5040'
  tertiary-fixed: '#f3e2af'
  tertiary-fixed-dim: '#d6c695'
  on-tertiary-fixed: '#231b00'
  on-tertiary-fixed-variant: '#514620'
  background: '#f8f9ff'
  on-background: '#0d1c2d'
  surface-variant: '#d4e4fa'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  price-display:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '700'
    lineHeight: 24px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  gutter: 16px
  margin-mobile: 16px
  margin-desktop: 32px
  touch-target: 48px
---

## Brand & Style
The design system focuses on utility, reliability, and precision for the B2B water delivery sector. The brand personality is industrial yet modern—replacing the "rough" nature of logistics with a clean, systematic interface that feels like a professional tool. 

The aesthetic is **Corporate Modern** with a focus on high-efficiency workflows. It utilizes a restrained color palette to reduce cognitive load during high-frequency tasks like route management and inventory tracking. The visual language emphasizes clarity through generous white space, structured grids, and a distinct lack of decorative elements, ensuring the "water" theme remains professional rather than lifestyle-oriented.

## Colors
The palette is rooted in deep aquatic tones to establish trust and stability.
- **Primary (#005F73):** A deep teal used for key actions, navigation states, and brand presence. It ensures high contrast for legibility.
- **Secondary (#94D2BD):** A soft aqua used for success states, active highlights, and subtle background fills to categorize "positive" data points.
- **Background (#F8FAFB):** A cool-toned neutral that prevents screen glare during outdoor use (common for delivery logistics).
- **Functional Colors:** Use standard semantic reds for "Low Water" alerts or "Late Delivery" statuses. Use the PKR currency symbol in the primary teal to emphasize financial transactions.

## Typography
Inter is selected for its exceptional legibility on mobile displays and its neutral, systematic character. 

The hierarchy is structured to prioritize data. Labels (like "Route ID" or "Unit Count") should use `label-caps` in a medium-grey neutral to provide context without competing with the primary data points. Financial information, specifically PKR values, should use `price-display` to ensure clear distinction from quantity counts. For Pakistani addresses, ensure the line height of `body-sm` is sufficient to handle multi-line location details clearly.

## Layout & Spacing
This design system uses an **8px linear scale** for all spacing and layout decisions. 

- **Mobile:** A 4-column fluid grid with 16px side margins. 
- **Desktop/Tablet:** A 12-column grid with a max-width of 1280px, centered.
- **Touch Targets:** Minimum 48x48px for all interactive elements to accommodate drivers and warehouse staff who may be using the app in fast-paced environments.
- **Data Density:** Use "Comfortable" spacing (16px) for customer-facing screens and "Compact" spacing (8px) for internal inventory manifests and delivery logs.

## Elevation & Depth
Elevation is communicated through **Tonal Layering** and **Low-Contrast Outlines**. 

- **Level 0 (Background):** #F8FAFB.
- **Level 1 (Cards/Surface):** White (#FFFFFF) with a 1px border of #E2E8F0. No shadow.
- **Level 2 (Active/Floating):** White (#FFFFFF) with a soft, diffused shadow: `0px 4px 12px rgba(0, 95, 115, 0.08)`. The shadow is tinted with the primary teal to maintain the water-inspired theme.
- **Modals:** High-elevation surfaces should use a backdrop dim of 40% opacity black to focus attention on the utility task.

## Shapes
A "Rounded" strategy (8px - 12px) is applied to soften the industrial nature of the app while maintaining a professional look. 

- **Standard Elements (Buttons, Inputs):** 8px (`rounded`).
- **Containers (Cards, Modals):** 16px (`rounded-lg`).
- **Search Bars/Chips:** 24px+ (`rounded-xl` or pill-shaped) to distinguish them from actionable data containers.
- **Consistency:** Use consistent corner radii for nested elements; if a card has a 16px radius, the internal button should keep its 8px radius to create a rhythmic nested appearance.

## Components
- **Buttons:** 
    - *Primary:* Filled with #005F73, white text, 48px height. 
    - *Secondary:* Outlined with 1.5px border in Primary color.
- **Input Fields:** 
    - Use "Outlined" style with floating labels. 
    - Phone inputs must be pre-formatted for Pakistani (+92) numbers with a clear country code prefix.
- **Cards:** 
    - Delivery cards should feature a prominent "Order ID" in the top left and a "Status Chip" in the top right. 
    - Bottom of cards should feature a primary action button (e.g., "Start Navigation").
- **Chips:** 
    - Use for status indicators (e.g., "Dispatched", "Delivered", "Canceled"). 
    - Backgrounds should be 12% opacity of the status color (Success/Green, Warning/Amber, Error/Red).
- **Lists:** 
    - High-density rows with a 1px bottom border. 
    - Include a chevron-right icon only if the row is navigable.
- **Specialty Components:** 
    - *Water Volume Gauge:* A horizontal progress bar in Secondary Aqua to visualize tank levels or truck capacity.
    - *Currency Display:* PKR symbol should always precede the amount with a non-breaking space.