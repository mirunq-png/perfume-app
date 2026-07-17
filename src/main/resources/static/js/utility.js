function formatText(input)
{
    if (typeof input !== 'string')
        input = String(input);
    return input.toLowerCase().split(' ').map(word=>word.charAt(0).toUpperCase()+word.slice(1)).join(' ');
}
(function setFavicon() // runs when utility.js loads
{
    if (!document.querySelector("link[rel~='icon']")) {
        const link = document.createElement('link');
        link.rel = 'icon';
        link.type = 'image/png';
        link.href = '/favicon.png';
        document.head.appendChild(link);
    }
})();