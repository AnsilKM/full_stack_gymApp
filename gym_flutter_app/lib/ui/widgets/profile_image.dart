import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';

class ProfileImage extends StatelessWidget {
  final String? imageUrl;
  final String name;
  final double size;

  const ProfileImage({
    super.key,
    required this.imageUrl,
    required this.name,
    this.size = 44,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.primary.withValues(alpha: 0.1),
        shape: BoxShape.circle,
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(size / 2),
        child: imageUrl != null && imageUrl!.isNotEmpty
            ? CachedNetworkImage(
                imageUrl: imageUrl!,
                fit: BoxFit.cover,
                placeholder: (context, url) => _Placeholder(name: name, size: size),
                errorWidget: (context, url, error) => _Placeholder(name: name, size: size),
              )
            : _Placeholder(name: name, size: size),
      ),
    );
  }
}

class _Placeholder extends StatelessWidget {
  final String name;
  final double size;

  const _Placeholder({required this.name, required this.size});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(size * 0.2),
      child: Image.asset(
        'assets/icon.png',
        fit: BoxFit.contain,
      ),
    );
  }
}
