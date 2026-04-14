const fs = require('fs');
const content = fs.readFileSync('MyOrders.vue', 'utf8');

// The template currently has:
// <template>
//   <Navbar />
//   <div class="myorders-page">...</div>
//   <Teleport>...</Teleport> × 3
//   <Footer />
// </template>
//
// Fix: wrap everything in a single root div

const templateStart = '<template>\n';
const templateEnd = '</template>\n';

const afterTemplate = content.indexOf(templateStart) + templateStart.length;
const beforeTemplateEnd = content.lastIndexOf(templateEnd);

const innerContent = content.substring(afterTemplate, beforeTemplateEnd);
const afterTemplateEnd = content.substring(beforeTemplateEnd + templateEnd.length);

// Wrap <Navbar /> into .myorders-page div, and <Footer /> + Teleports inside root wrapper
// New structure: single root div wrapping Navbar + myorders-page content + Footer
// But Teleport needs to stay at same level

// Actually simpler: just change the structure so Navbar is INSIDE myorders-page,
// following the same pattern as Contact.vue which has Navbar inside the wrapper div

// Current inner: starts with "  <Navbar />\n  <div class=\"myorders-page\">"
// Target: "<div class=\"myorders-page\">\n  <Navbar />\n    ... old myorders-page content ... \n  <Footer />\n</div>"

// Replace: move Navbar inside myorders-page, move Footer inside myorders-page
// and wrap Teleports inside the root div too

let fixed = innerContent;

// Remove leading Navbar outside wrapper
fixed = fixed.replace('  <Navbar />\n  <div class="myorders-page">', '  <div class="myorders-page">\n    <Navbar />');

// Move closing </div> of myorders-page before Footer and Teleports
// The structure now ends with:
//   </div>   <- closes orders-content
// </div>     <- closes myorders-page
// <Teleport>...</Teleport> x3
// <Footer />

// We need to:
// 1. Keep Footer INSIDE myorders-page (before its closing tag)
// 2. Keep Teleports OUTSIDE (they teleport to body anyway)

// Move <Footer /> inside the myorders-page closing tag
fixed = fixed.replace('\n  <Footer />\n', '\n    <Footer />\n  ');

// Now wrap everything in single fragment using a container
// Actually the simplest fix: wrap ALL in a root div
const newTemplate = templateStart + 
  '<div class="app-view">\n' +
  fixed +
  '</div>\n' +
  templateEnd;

const newContent = newTemplate + afterTemplateEnd;
fs.writeFileSync('MyOrders.vue', newContent, 'utf8');
console.log('Done! New file length:', newContent.length);
// Show first 500 chars of template
console.log('Template start:\n', newContent.substring(0, 500));
