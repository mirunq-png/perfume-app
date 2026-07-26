export function formatText(input) {
  if (typeof input !== 'string') input = String(input);
  return input
    .toLowerCase()
    .split(' ')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}
