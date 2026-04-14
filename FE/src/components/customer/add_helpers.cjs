const fs = require('fs');
const content = fs.readFileSync('MyOrders.vue', 'utf8');

const addedCode = `
// ── Pagination helper ──
const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value + 1
  const pages = []
  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    pages.push(1)
    if (current > 3) pages.push('...')
    for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) pages.push(i)
    if (current < total - 2) pages.push('...')
    pages.push(total)
  }
  return pages
})

// ── Date helpers ──
const formatDateShort = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
}
`;

// Insert before </script>
const scriptClose = '</script>';
const idx = content.indexOf(scriptClose);
console.log('</script> found at index:', idx);

const newContent = content.substring(0, idx) + addedCode + '\n' + scriptClose + content.substring(idx + scriptClose.length);
fs.writeFileSync('MyOrders.vue', newContent, 'utf8');
console.log('Done! New file length:', newContent.length);
